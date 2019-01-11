package stone.dal.kernel.utils;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by  on 12/23/2016.
 */
public class CollectionUtilsTest {
  @Test
  public void testSort() {
    List<User> objList = new ArrayList<>();
    objList.add(new User(5l));
    objList.add(new User(6l));
    objList.add(new User(1l));

    CollectionUtils.sort(objList, "id", true);
    Assert.assertEquals(new Long(1), objList.get(0).getId());
    Assert.assertEquals(new Long(6), objList.get(2).getId());

    CollectionUtils.sort(objList, "id", false);
    Assert.assertEquals(new Long(6), objList.get(0).getId());
    Assert.assertEquals(new Long(1), objList.get(2).getId());
  }
}
