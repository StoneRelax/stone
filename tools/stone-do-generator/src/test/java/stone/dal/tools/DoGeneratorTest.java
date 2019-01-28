package stone.dal.tools;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class DoGeneratorTest {

  private DoGenerator doGenerator;

  private File file;

  @Before
  public void init() {
    doGenerator = new DoGenerator();
    file = new File("do-meta.xlsx");
  }

  @Test
  public void testBuild() throws Exception {
    doGenerator.build("do-meta.xlsx", null, "stone.dal.pojo");
    System.out.println("in test");
  }

}
