package stone.dal.tools;

import org.junit.Before;
import org.junit.Test;
import stone.dal.tools.meta.RawEntityMeta;

import java.io.File;
import java.util.List;

public class DoGeneratorTest {

  private DoGenerator doGenerator;

  private File file;

  @Before
  public void init() {
    doGenerator = new DoGenerator();
    file = new File("ebayuniversity-entities.xlsx");
  }

  @Test
  public void testBuild() throws Exception {
    doGenerator.build(file);
    System.out.println("in test");
  }
}
