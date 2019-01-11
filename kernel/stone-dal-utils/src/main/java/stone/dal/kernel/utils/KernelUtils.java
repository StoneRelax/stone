package stone.dal.kernel.utils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author fengxie
 */
public abstract class KernelUtils {

  public static boolean bool_v(Boolean v) {
    return ObjectUtils.unboxBool(v);
  }

  public static boolean bool_v(Integer v) {
    return new Integer(1).equals(v);
  }

  public static boolean list_emp(Collection collection) {
    return org.apache.commons.collections.CollectionUtils.isEmpty(collection);
  }

  public static boolean arr_emp(Object[] arr) {
    return ArrayUtils.isEmpty(arr);
  }

  public static String list_2_str(List arr, String separator) {
    return StringUtils.combineString(arr, separator);
  }

  public static String[] str_2_arr(String str, String separator) {
    return StringUtils.splitString2Array(str, separator);
  }

  public static String arr_2_str(Object[] arr, String separator) {
    return StringUtils.combineString(arr, separator);
  }

  public static boolean map_emp(Map map) {
    return MapUtils.isEmpty(map);
  }

  public static boolean str_emp(String s) {
    return StringUtils.isEmpty(s);
  }

  public static String repl_emp(Object s) {
    return StringUtils.replaceNull(s);
  }

  public static String date_2_str_pattern(Date date, String pattern) {
    return ConvertUtils.date2Str(date, pattern);
  }

  public static int int_v(String s) {
    return Integer.parseInt(s);
  }

  public static void set_v(Object obj, String propertyName, Object v) {
    ObjectUtils.setPropertyValue(obj, propertyName, v);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get_v(Object obj, String propertyName) {
    return (T) ObjectUtils.getPropertyValue(obj, propertyName);
  }

  public static Object get_v_recursive(Object obj, String propertyName) {
    return ObjectUtils.getPropertyValue(obj, propertyName);
  }

  public static void set_v_recursive(Object obj, String propertyName, Object v) {
    ObjectUtils.setPropertyValue(obj, propertyName, v);
  }

  public static String replace(String s, String search, String with) {
    return org.apache.commons.lang.StringUtils.replace(s, search, with);
  }

}