package stone.dal.tools;

import org.junit.Test;

public class DoGeneratorTest {

  @Test
  public void testBuild() throws Exception {
    DoGenerator doGenerator = new DoGenerator();
    doGenerator.build("entities.xlsx", "./target", null, null, "extension-rules.yaml");
  }
}
