package stone.dal.seq.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.seq.api.meta.SequenceMeta;

/**
 * Created by on 5/4/2017.
 */
public class SequenceSeedGeneratorImplTest {
  private static final int THREAD_NUM = 100;

  private static final int REPEAT_PER_THREAD_NUM = 1500;

  @Before
  public void setup() throws Exception {
    File dir = new File("out/sequence");
    FileUtils.deleteDirs(dir);
  }

  @Test
  public void testSequenceSeed() throws Exception {
    runSequenceAcquire(0);
    runSequenceAcquire(THREAD_NUM * REPEAT_PER_THREAD_NUM + 1);
    byte[] res = FileUtils.readFile(new File("out/sequence/test.seed").getAbsolutePath());
    Assert.assertEquals("300100", new String(res));
  }

  private void runSequenceAcquire(long start) throws Exception {
    SequenceMeta seqMeta = SequenceMeta.factory().id("test").start(0l).step(1).circle(false).build();
    File file = new File("out");
    SequenceSeed seed = new SequenceSeed(file.getAbsolutePath(), seqMeta);
    long startTs = System.currentTimeMillis();
    int[] seeds = new int[THREAD_NUM * REPEAT_PER_THREAD_NUM];
    List<Runnable> threads = new ArrayList<>();
    CyclicBarrier barrier = new CyclicBarrier(THREAD_NUM + 1);
    for (int i = 0; i < THREAD_NUM; i++) {
      threads.add(() -> {
        for (int j = 0; j < REPEAT_PER_THREAD_NUM; j++) {
          long seedNum = seed.acquire(start);
          seeds[(int) (seedNum - start) - 1] = 1;
        }
        try {
          barrier.await();
        } catch (Exception e) {
          Assert.fail(e.getMessage());
        }
      });
    }

    for (Runnable future : threads) {
      new Thread(future).start();
    }
    barrier.await();
    for (int _seed : seeds) {
      Assert.assertEquals(_seed, 1);
    }
    long end = System.currentTimeMillis();
    Assert.assertTrue("cost time should be less than 3s", (end - startTs) <= 3000);
  }
}
