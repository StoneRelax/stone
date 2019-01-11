package stone.dal.kernel.utils;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import stone.dal.kernel.utils.scantest.Foo1;
import stone.dal.kernel.utils.scantest.Foo2;

/**
 * @author fengxie
 */
public class UrlUtilsTest {

  @Test
  public void testFindClassesByPackage() throws Exception {
    Set<Class> classes = UrlUtils.findClassesByPackage("stone.dal.kernel.utils.scantest");
    Assert.assertTrue(classes.contains(Foo1.class));
    Assert.assertTrue(classes.contains(Foo2.class));
    Assert.assertEquals(2, classes.size());
  }
}
