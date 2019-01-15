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
 * @author fengxie
 */
public class SequenceMixGeneratorImplTest {
  private static final int THREAD_NUM = 100;

  private static final int REPEAT_PER_THREAD_NUM = 1500;

  @Before
  public void setup() throws Exception {
    File dir = new File("out/sequence");
    FileUtils.deleteDirs(dir);
    File file = new File("out/sequence/testFormat.seed");
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void testFormat() throws InvalidInputException, UndefinedSeqException {
    SequenceMeta seqMeta = SequenceMeta.factory().id("testFormat").type("mix")
        .format("${date} ${sequence} $F{data}").datePattern("yyyy-MM-dd").start(0l).end(10l).step(1).circle(true)
        .build();
    Set<SequenceMeta> sequenceMetaSet = new HashSet<>();
    sequenceMetaSet.add(seqMeta);
    SequenceMixGeneratorImpl sequenceSeedGenerator = new SequenceMixGeneratorImpl(new File("out").getAbsolutePath(),
        sequenceMetaSet);

    Map<String, String> context = new HashMap<>();
    context.put("data", "testData");
    for (int n = 0; n < 3; n++) {
      for (int i = 0; i < 10; i++) {
        String res = sequenceSeedGenerator.next("testFormat", context);
        String expectedResult = ConvertUtils.date2Str(new Date(), "yyyy-MM-dd") + " " + ((i + 1)) + " testData";
        Assert.assertEquals(expectedResult, res);
      }
    }
  }

}
