package stone.dal.kernel;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import stone.dal.kernel.scantest.Foo1;
import stone.dal.kernel.scantest.Foo2;

/**
 * @author fengxie
 */
public class UrlUtilsTest {

  @Test
  public void testFindClassesByPackage() throws Exception {
    Set<Class> classes = UrlUtils.findClassesByPackage("stone.dal.kernel.scantest");
    Assert.assertTrue(classes.contains(Foo1.class));
    Assert.assertTrue(classes.contains(Foo2.class));
    Assert.assertEquals(2, classes.size());
  }
}
