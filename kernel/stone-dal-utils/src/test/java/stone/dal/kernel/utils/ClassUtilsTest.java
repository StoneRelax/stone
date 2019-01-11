package stone.dal.kernel.utils;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class ClassUtilsTest {

  @Test
  public void testGetResources() throws IOException {
    Set<URL> jsFiles = ClassUtils.getResources("stone.dal.kernel.utils.scantest", "js");
    Assert.assertEquals(2, jsFiles.size());
    Set<String> foundSet = new HashSet<>();
    jsFiles.forEach(file -> {
      try {
        String content = StringUtils.readInputStream(file.openStream());
        foundSet.add(content);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    Assert.assertEquals(2, foundSet.size());
    Assert.assertTrue(foundSet.contains("alert(\"test!\");"));
    Assert.assertTrue(foundSet.contains("alert(\"test1!\");"));
  }
}
