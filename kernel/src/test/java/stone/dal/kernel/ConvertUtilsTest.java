package stone.dal.kernel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class ConvertUtilsTest {

  @Test
  public void testDate2Str() {
    Date date = ConvertUtils.str2Date("2015-01-01");
    Assert.assertEquals("2015-01-01", ConvertUtils.date2Str(date));
  }

  @Test
  public void testMapImEx() {
    Group group = new Group();
    group.setId(System.currentTimeMillis());
    group.setName("group");

    User user1 = new User();
    user1.setId(System.currentTimeMillis());
    user1.setName("user1");
    user1.setBirthday(new Date());
    user1.setGroup(group);
    group.addUser(user1);

    User user2 = new User();
    user2.setId(System.currentTimeMillis());
    user2.setName("user2");
    user2.setBirthday(new Date());
    user2.setGroup(group);
    group.addUser(user2);

    Map groupMap = ConvertUtils.obj2Map(group);
    Assert.assertEquals(group.getName(), groupMap.get("name"));
    Assert.assertEquals(group.getId(), groupMap.get("id"));
    Assert.assertEquals(2, ((List) groupMap.get("users")).size());
    Map _user1 = (Map) ((List) groupMap.get("users")).get(0);
    Assert.assertEquals(user1.getName(), _user1.get("name"));
    Assert.assertNull(_user1.get("group"));

    Map _user2 = (Map) ((List) groupMap.get("users")).get(1);
    Assert.assertEquals(user2.getBirthday(), _user2.get("birthday"));

    Group newGroup = (Group) ConvertUtils.map2Obj(groupMap, Group.class);
    System.out.println(newGroup);

    Assert.assertEquals(newGroup.getName(), group.getName());
    Assert.assertEquals(newGroup.getId(), group.getId());
    Assert.assertEquals(newGroup.getUsers().get(0).getBirthday(), group.getUsers().get(0).getBirthday());
    Assert.assertEquals(newGroup.getUsers().get(0).getName(), group.getUsers().get(0).getName());
    Assert.assertEquals(newGroup.getUsers().get(1).getBirthday(), group.getUsers().get(1).getBirthday());
    Assert.assertEquals(newGroup.getUsers().get(1).getName(), group.getUsers().get(1).getName());
  }
}
