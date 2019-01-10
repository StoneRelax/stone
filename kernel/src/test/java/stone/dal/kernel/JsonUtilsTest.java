package stone.dal.kernel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonUtilsTest {
  private Group group;

  private Map map;

  @Before
  @SuppressWarnings("unchecked")
  public void init() {
    group = new Group();
    group.setId(1l);
    group.setName("name");
    User user1 = new User();
    user1.setId(1l);
    user1.setName("user1");
    user1.setBirthday(new Date());
    user1.setCreateTime(new Timestamp(new Date().getTime()));
    user1.setColor(Color.blue);

    User user2 = new User();
    user2.setId(2l);
    user2.setName("user2");
    user2.setBirthday(new Date());
    user2.setCreateTime(new Timestamp(new Date().getTime()));
    user2.setColor(Color.red);

    group.addUser(user1);
    user1.setGroup(group);
    group.addUser(user2);
    user2.setGroup(group);

    map = new HashMap();
    map.put("1", 1);
    map.put("2", "2");
    map.put("3", new Date());
  }

  @Test
  public void testList() {
    SerializeConfig mapping = new SerializeConfig();
    String dateFormat = "yyyy-MM-dd";
    mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    mapping.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
    String jsonString = JsonUtils.toJson(group.getUsers(), mapping, false);
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    String today = sdf.format(new Date());
    String expect =
        "[{\"birthday\":\"" + today + "\",\"color\":{\"r\":0,\"g\":0,\"b\":255,\"alpha\":255},\"createTime\":\"" +
            today + "\",\"id\":1,\"name\":\"user1\"},{\"birthday\":\"" + today +
            "\",\"color\":{\"r\":255,\"g\":0,\"b\":0,\"alpha\":255},\"createTime\":\"" + today +
            "\",\"id\":2,\"name\":\"user2\"}]";
    Assert.assertEquals(jsonString, expect);
  }

  @Test
  public void testObj() {
    SerializeConfig mapping = new SerializeConfig();
    String dateFormat = "yyyy-MM-dd";
    mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    mapping.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
    String jsonString = JsonUtils.toJson(group, mapping, false);
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    String today = sdf.format(new Date());
    String expect = "{\"id\":1,\"name\":\"name\",\"users\":[{\"birthday\":\"" + today +
        "\",\"color\":{\"r\":0,\"g\":0,\"b\":255,\"alpha\":255},\"createTime\":\"" + today +
        "\",\"id\":1,\"name\":\"user1\"},{\"birthday\":\"" + today +
        "\",\"color\":{\"r\":255,\"g\":0,\"b\":0,\"alpha\":255},\"createTime\":\"" + today +
        "\",\"id\":2,\"name\":\"user2\"}]}";
    Assert.assertEquals(jsonString, expect);

    Group _group = JsonUtils.fromJson(jsonString, Group.class);
    Assert.assertNotNull(_group);
    Assert.assertTrue(!_group.getUsers().isEmpty());
  }

  @Test
  public void testMap() {
    SerializeConfig mapping = new SerializeConfig();
    String dateFormat = "yyyy-MM-dd";
    mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    mapping.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
    String jsonString = JsonUtils.toJson(map, mapping, true);
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    String today = sdf.format(new Date());
    Map _map = JSON.parseObject(jsonString, Map.class);
    Assert.assertNotNull(_map);
    Assert.assertEquals(_map.get("3"), today);
  }
}
