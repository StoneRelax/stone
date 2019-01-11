package stone.dal.kernel.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JsonUtils
 */
public class JsonUtils {
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private static SerializeConfig mapping = new SerializeConfig();

  static {
    mapping.put(Date.class, new SimpleDateFormatSerializer(DATE_FORMAT));
    mapping.put(Timestamp.class, new SimpleDateFormatSerializer(TIME_FORMAT));
  }

  public static String toJson(Object obj) {
    return toJson(obj, mapping, true);
  }

  /**
   * Convert object to Json object
   *
   * @param obj          Object
   * @param config       Serialize config
   * @param prettyFormat Should be format
   * @return String in json format
   */
  public static String toJson(Object obj, SerializeConfig config, boolean prettyFormat) {
    return toJson(obj, config, prettyFormat, false);
  }

  public static String toJson(Object obj, SerializeConfig config, boolean prettyFormat, boolean writeType) {
    List<SerializerFeature> features = new ArrayList<>();
    if (prettyFormat) {
      features.add(SerializerFeature.PrettyFormat);
    }
    if (writeType) {
      features.add(SerializerFeature.WriteClassName);
    }
    return toJson(obj, config, features.toArray(new SerializerFeature[features.size()]));
  }

  public static String toJson(Object obj, SerializeConfig config, SerializerFeature[] features) {
    String json;
    if (features != null) {
      json = JSON.toJSONString(obj, config, features);
    } else {
      json = JSON.toJSONString(obj, config);
    }
    return json;
  }

  @SuppressWarnings("unchecked")
  public static <T> T fromJson(String json) {
    return (T) JSON.parseObject(json);
  }

  @SuppressWarnings("unchecked")
  public static <T> T fromJson(String json, Class clazz) {
    DefaultJSONParser parser = new DefaultJSONParser(json, ParserConfig.getGlobalInstance());
    parser.setDateFormat(DATE_FORMAT);
    T v = (T) parser.parseObject(clazz);
    parser.handleResovleTask(v);
    parser.close();
    return v;
  }

  /**
   * from json list
   *
   * @param json     json string
   * @param itemType item type in list
   * @return list objects
   */
  public static List fromJsonList(String json, Class itemType) {
    return JSON.parseArray(json, itemType);
  }
}
