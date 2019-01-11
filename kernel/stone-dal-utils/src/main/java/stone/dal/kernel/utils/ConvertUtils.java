package stone.dal.kernel.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengxie
 */
public class ConvertUtils {

  private static Logger logger = LoggerFactory.getLogger(ConvertUtils.class);

  private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

  private static final String SHORT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

  /**
   * Format Date , Patten: 'YYYY-MM-DD' Locale: US
   *
   * @param dat Date to formate
   * @return Formatted Date
   */
  public static String date2Str(final Date dat) {
    return date2Str(dat, SHORT_DATE_FORMAT, Locale.US);
  }

  /**
   * Convert date 2 string
   *
   * @param date Date
   * @return yyyy-MM-dd HH:mm
   */
  public static String dateTime2Str(final Date date) {
    return date2Str(date, SHORT_DATE_TIME_FORMAT, Locale.US);
  }

  /**
   * Format Date according to Patter and Locale of US
   *
   * @param dat     Date to format
   * @param pattern Pattern of Date to format
   * @return Formatted Date
   */
  public static String date2Str(final Date dat, final String pattern) {
    return date2Str(dat, pattern, Locale.US);
  }

  /**
   * Format String in 'YYYY-MM-DD' to Date
   *
   * @param dateStr String to Convert
   * @return Formatted Date
   */
  public static Date str2Date(final String dateStr) {
    return str2Date(dateStr, SHORT_DATE_FORMAT, Locale.US);
  }

  /**
   * Format String in 'YYYY-MM-DD HH:mm' to Date
   *
   * @param dateStr String to Convert
   * @return Formatted Date
   */
  public static Date str2DateTime(final String dateStr) {
    return str2Date(dateStr, "yyyy-MM-dd HH:mm", Locale.US);
  }

  /**
   * Formate String in Pattern to Date
   *
   * @param dateStr String to Convert
   * @param pattern Convert Pattern
   * @return Formatted Date
   */
  public static Date str2Date(final String dateStr, final String pattern) {
    return str2Date(dateStr, pattern, Locale.US);
  }

  /**
   * Formate String in Pattern and Locale to Date
   *
   * @param dateStr String to Convert
   * @param pattern Convert Pattern
   * @param loc     Convert Locale
   * @return Formatted Date
   */
  public static Date str2Date(
      final String dateStr, final String pattern,
      final Locale loc) {
    if (StringUtils.isEmpty(dateStr)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(pattern, loc);
      return sdf.parse(dateStr);
    } catch (ParseException e) {
      LogUtils.error(logger, e);
      return null;
    }
  }

  /**
   * Formate String in 'YYYY-MM-DD' to Timestemp
   *
   * @param dateStr String to Convert
   * @return Formatted Timestemp
   */
  public static Timestamp str2Timestamp(final String dateStr) {
    Date dat = str2Date(dateStr);
    return new Timestamp(dat.getTime());
  }

  /**
   * Formate String to Number
   *
   * @param strNum String to be converted
   * @return Number or NULL
   */
  public static String str2Num(final String strNum) {
    NumberFormat nbf = new DecimalFormat();
    try {
      if (strNum == null) {
        return "0";
      } else {
        return nbf.parse(strNum).toString();
      }
    } catch (ParseException e) {
      logger.warn("error!");
      return null;
    }
  }

  /**
   * Formate Date accoring to Pattern and Locale
   *
   * @param dat     Date to formate
   * @param pattern Pattern of Date to Format
   * @param loc     Locale of Date to format
   * @return Formatted Date
   */
  public static String date2Str(
      final Date dat, final String pattern,
      final Locale loc) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, loc);
    if (dat != null) {
      return sdf.format(dat);
    } else {
      return "";
    }
  }

  /**
   * Conver property string value to object.
   *
   * @param str   Property value of string
   * @param clazz Property class
   * @return Property value
   */
  @SuppressWarnings("unchecked")
  public static <T> T str2Obj(
      String str, Class clazz) {
    Object value = str;
    if (clazz != null) {
      if (clazz == Timestamp.class) {
        value = new Timestamp(str2Date(str).getTime());
      } else if (clazz == Date.class) {
        value = str2Date(str);
      } else if (clazz == Long.class || clazz == long.class) {
        value = new Long(str);
      } else if (clazz == Integer.class || clazz == int.class) {
        value = new Integer(str);
      } else if (clazz == BigDecimal.class) {
        value = new BigDecimal(str);
      } else if (clazz == Boolean.class || clazz == boolean.class) {
        value = Boolean.valueOf(str);
      }
    }
    return (T) value;
  }

  /**
   * Convert property string value to object.
   *
   * @param value       Property value of string
   * @param datePattern Date pattern
   * @return Property value
   */
  public static String obj2Str(Object value, String datePattern) {
    String strValue = null;
    if (value != null) {
      if (value instanceof Timestamp) {
        strValue = dateTime2Str((Timestamp) value);
      } else if (value instanceof Date) {
        strValue = date2Str((Date) value, datePattern);
      } else {
        strValue = value.toString();
      }
    }
    return strValue;
  }

  private static Object convertValWhenObj2Map(PropertyDescriptor pd, Object obj, Options options) {
    if (options != null) {
      if (options.saveTimeStampAsLong()) {
        if (obj instanceof Timestamp) {
          return ((Timestamp) obj).getTime();
        }
      }
    }
    return obj;
  }

  private static Object convertValWhenMap2Obj(PropertyDescriptor pd, Object obj, Options options) {
    if (options != null) {
      if (options.saveTimeStampAsLong()) {
        Class clazz = pd.getPropertyType();
        if (obj instanceof Long && clazz.equals(Timestamp.class)) {
          return new Timestamp((Long) obj);
        }
      }
    }
    return obj;
  }

  public static Map<String, Object> obj2Map(Object obj) {
    return obj2Map(obj, null);
  }

  /**
   * Export property value of specified object to a map
   *
   * @param obj object instance
   * @return map instance includes property value
   */
  public static Map<String, Object> obj2Map(Object obj, Options options) {
    return obj2Map(obj, options, new Stack<>());
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> obj2Map(Object obj, Options options, Stack<Object> parents) {
    parents.push(obj);
    Map<String, Object> mapOutput = new HashMap<>();
    if (!obj.getClass().isAssignableFrom(Map.class)) {
      try {
        PropertyDescriptor[] propertyDescriptors = org.springframework.beans.BeanUtils
            .getPropertyDescriptors(obj.getClass());
        for (PropertyDescriptor descriptor : propertyDescriptors) {
          String name = descriptor.getName();
          if (!"class".equals(name)) {
            Class clazz = descriptor.getPropertyType();
            Method getter = descriptor.getReadMethod();
            if (getter != null && !name.startsWith("_")) {
              Object value = getter.invoke(obj);
              if (value != null) {
                if (ClassUtils.isPrimitive(clazz)) {
                  mapOutput.put(name, convertValWhenObj2Map(descriptor, value, options));
                } else if (clazz.isEnum()) {
                  mapOutput.put(name, ((Enum) value).name());
                } else if (Collection.class.isAssignableFrom(clazz)) {
                  Collection<Map> collection;
                  if (Set.class.isAssignableFrom(clazz)) {
                    collection = new HashSet<>();
                  } else {
                    collection = new ArrayList<>();
                  }
                  ((Collection) value).forEach(elm -> {
                    collection.add(obj2Map(elm, options, parents));
                  });
                  if (!org.apache.commons.collections.CollectionUtils.isEmpty(collection)) {
                    mapOutput.put(name, collection);
                  }
                } else if (!clazz.isArray()) {
                  if (!parents.contains(value)) {
                    mapOutput.put(name, obj2Map(value, options, parents));
                  }
                }
              }
            }
          }
        }
      } catch (Exception ex) {
        throw new KernelRuntimeException(ex);
      }
    }
    parents.pop();
    return mapOutput;
  }

  public static Object map2Obj(Map<String, Object> map, Class clazz) {
    return map2Obj(map, clazz, null);
  }

  @SuppressWarnings("unchecked")
  public static Object map2Obj(Map<String, Object> map, Class clazz, Options options) {
    Object obj = null;
    try {
      obj = clazz.newInstance();
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    }
    PropertyDescriptor[] propertyDescriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      String name = descriptor.getName();
      Class pClazz = descriptor.getPropertyType();
      Method setter = descriptor.getWriteMethod();
      if (setter != null) {
        Object value = map.get(name);
        if (value != null) {
          if (ClassUtils.isPrimitive(pClazz)) {
            ObjectUtils.setPropertyValue(obj, name, convertValWhenMap2Obj(descriptor, value, options));
          } else if (pClazz.isEnum()) {
            ObjectUtils.setPropertyValue(obj, name, Enum.valueOf(pClazz, value.toString()));
          } else if (Collection.class.isAssignableFrom(pClazz)) {
            Collection<Object> collection;
            if (Set.class.isAssignableFrom(pClazz)) {
              collection = new HashSet<>();
            } else {
              collection = new ArrayList<>();
            }
            ((Collection<Map>) value).forEach(elm -> {
              try {
                collection.add(map2Obj(elm, ClassUtils.getCollectionType(clazz, name), options));
              } catch (ClassNotFoundException e) {
                throw new KernelRuntimeException(e);
              }
            });
            ObjectUtils.setPropertyValue(obj, name, collection);
          } else if (!pClazz.isArray()) {
            Object pValue = map2Obj((Map<String, Object>) value, pClazz, options);
            ObjectUtils.setPropertyValue(obj, name, pValue);
          }
        }
      }
    }
    return obj;
  }

  public final static class Options {
    boolean saveTimeStampAsLong;

    public boolean saveTimeStampAsLong() {
      return saveTimeStampAsLong;
    }

  }

  public static class Factory {

    private Options options = new Options();

    public static Factory getInstance() {
      return new Factory();
    }

    public Factory saveTimeStampAsLong(boolean saveTimeStampAsLong) {
      options.saveTimeStampAsLong = saveTimeStampAsLong;
      return this;
    }

    public Options build() {
      return options;
    }
  }

}
