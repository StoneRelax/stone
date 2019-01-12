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

  public static boolean boolValue(Boolean v) {
    return ObjectUtils.unboxBool(v);
  }

  public static boolean boolValue(Integer v) {
    return new Integer(1).equals(v);
  }

  public static boolean isCollectionEmpty(Collection collection) {
    return org.apache.commons.collections.CollectionUtils.isEmpty(collection);
  }

  public static boolean isArrayEmpty(Object[] arr) {
    return ArrayUtils.isEmpty(arr);
  }

  public static String list2Str(List arr, String separator) {
    return StringUtils.combineString(arr, separator);
  }

  public static String[] str2Arr(String str, String separator) {
    return StringUtils.splitString2Array(str, separator);
  }

  public static String arr2Str(Object[] arr, String separator) {
    return StringUtils.combineString(arr, separator);
  }

  public static boolean isMapEmpty(Map map) {
    return MapUtils.isEmpty(map);
  }

  public static boolean isStrEmpty(String s) {
    return StringUtils.isEmpty(s);
  }

  public static String replaceNull(Object s) {
    return StringUtils.replaceNull(s);
  }

  public static String date2Str(Date date, String pattern) {
    return ConvertUtils.date2Str(date, pattern);
  }

  public static void setPropVal(Object obj, String propertyName, Object v) {
    ObjectUtils.setPropertyValue(obj, propertyName, v);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getPropVal(Object obj, String propertyName) {
    return (T) ObjectUtils.getPropertyValue(obj, propertyName);
  }

  public static String replace(String s, String search, String with) {
    return org.apache.commons.lang.StringUtils.replace(s, search, with);
  }

}