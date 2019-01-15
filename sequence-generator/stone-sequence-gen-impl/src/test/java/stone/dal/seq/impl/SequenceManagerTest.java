package stone.dal.seq.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import stone.dal.kernel.utils.ConvertUtils;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.seq.api.ex.InvalidInputException;
import stone.dal.seq.api.ex.UndefinedSeqException;
import stone.dal.seq.api.meta.SequenceMeta;

/**
 * Created by on 5/8/2017.
 */
public class SequenceManagerTest {
  @Before
  public void setup() throws Exception {
    File dir = new File("out/sequence");
    FileUtils.deleteDirs(dir);
  }

  @Test
  public void test() throws InvalidInputException, UndefinedSeqException {
    SequenceMeta mixSeq = SequenceMeta.factory().id("mix").type("mix")
        .format("${date} ${sequence} $F{data}").datePattern("yyyy-MM-dd").start(0l).end(10l).step(1).circle(true)
        .build();
    SequenceMeta seedSeq = SequenceMeta.factory().id("seed").type("seed").start(0l).step(1).circle(false).build();
    SequenceMeta uuidSeq = SequenceMeta.factory().id("uuid").type("uuid").build();

    Set<SequenceMeta> sequenceMetaSet = new HashSet<>();
    sequenceMetaSet.add(mixSeq);
    sequenceMetaSet.add(seedSeq);
    sequenceMetaSet.add(uuidSeq);

    SequenceManagerImpl sequenceManager = new SequenceManagerImpl(new File("out").getAbsolutePath(), sequenceMetaSet);
    Map<String, String> context = new HashMap<>();
    context.put("data", "testData");
    //test for mix
    for (int n = 0; n < 3; n++) {
      for (int i = 0; i < 10; i++) {
        String res = (String) sequenceManager.getGenerator("mix").next("mix", context);
        String expectedResult = ConvertUtils.date2Str(new Date(), "yyyy-MM-dd") + " " + ((i + 1)) + " testData";
        Assert.assertEquals(expectedResult, res);
      }
    }
    //test for seed
    for (int i = 0; i < 100; i++) {
      Assert.assertEquals((long) (i + 1), sequenceManager.getGenerator("seed").next("seed", null));
    }
    //test for uuid
    String res = (String) sequenceManager.getGenerator("uuid").next("uuid", null);
    Assert.assertNotNull(res);
  }
}
