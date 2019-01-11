/*
 * File: $RCSfile: CollectionUtilities.java,v $
 *
 * Copyright (c) 2015 Dr0ne,
 *
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Dr0ne ("Confidential Information"). You shall notCopyright (c) 2015 Dr0ne
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with Dr0ne.
 */
package stone.dal.kernel.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.collections.map.ListOrderedMap;

/**
 * The class <code>CollectionUtils</code> provides utilities methods for collection operation
 *
 * @author feng.xie
 * @version $Revision: 1.10 $
 */
public class CollectionUtils {

  /**
   * Private constructor
   */
  private CollectionUtils() {
  }

  /**
   * Return serializable data, the format of these data is placed like the following format
   * ${key}=${value}
   *
   * @param properties Properties instance
   * @return serializable data
   * @throws UnsupportedEncodingException if errors occure during text converted to UTF-8 encoding
   */
  @SuppressWarnings("unchecked")
  public static byte[] encodeProperties(Properties properties) throws UnsupportedEncodingException {
    Set keySet = properties.keySet();
    TreeSet keys = new TreeSet(keySet);
    StringBuilder bf = new StringBuilder();
    keys.stream().forEach((item) -> {
      String resourceValue = (String) properties.get(item);
      bf.append(item);
      bf.append("=");
      bf.append(resourceValue);
      bf.append("\n");
    });
    return bf.toString().getBytes("UTF-8");
  }

  /**
   * Return min value from int array
   *
   * @param values Values
   * @return Min value
   */
  public static int getMinValue(int[] values) {
    final int[] minIndex = { Integer.MAX_VALUE };
    Arrays.stream(values).forEach((value) -> {
      if (minIndex[0] > value) {
        minIndex[0] = value;
      }
    });
    return minIndex[0];
  }

  /**
   * Return max value from int array
   *
   * @param values Values
   * @return Max value
   */
  public static int getMaxValue(int[] values) {
    final int[] maxIndex = { Integer.MIN_VALUE };
    Arrays.stream(values).forEach((value) -> {
      if (maxIndex[0] < value) {
        maxIndex[0] = value;
      }
    });
    return maxIndex[0];
  }

  /**
   * Convert array to a new list
   *
   * @param src source array
   * @return new list
   */
  public static List convert2List(Object[] src) {
    List lstResult = new ArrayList();
    org.apache.commons.collections.CollectionUtils.addAll(lstResult, src);
    return lstResult;
  }

  /**
   * List src substract list des
   *
   * @param src Source
   * @param des Removed list
   * @return substract result
   */
  @SuppressWarnings("unchecked")
  public static List sub(List src, List des) {
    List result = new ArrayList();
    src.stream().forEach((item) -> {
      if (!des.contains(item)) {
        result.add(item);
      }
    });
    return result;
  }

  /**
   * seperate source into two string with specified seperator , and put them into
   * to a map with first string as key, and second as value
   *
   * @param map       result map
   * @param source    source string
   * @param separator separator string
   */
  public static void fillMapWithSeparator(Map map, String source, String separator) {
//        String[] pair = StringUtilities.splitString2Array(source, separator);
//        if (pair.length == 2) {
//            try {
//                String key = pair[0];
//                String value = pair[1];
//                map.put(key, value);
//            } catch (Exception e) {
//                tracer.info("Pair found error");
//            }
//        }
  }

  /**
   * Safety add one element into collection, if collection contains specified object, it doesn't add it
   *
   * @param collection specified collection
   * @param object     specified object
   * @return @see java.util.List#add
   */
  @SuppressWarnings("unchecked")
  public static boolean safeAdd(List collection, Object object) {
    return object != null && !collection.contains(object) && collection.add(object);
  }

  /**
   * Safety add a source collection to target
   *
   * @param target Target collection
   * @param source Source collection
   */
  public static void safeAddAll(List<?> target, List<?> source) {
    source.stream().forEach(item -> safeAdd(target, item));
  }

  /**
   * Check whether map contains value
   *
   * @param map specified map
   * @return boolean value
   */
  public static boolean isRealEmpty(Map map) {
    boolean isEmpty = true;
    if (map != null) {
      Set keySet = map.keySet();
      for (Object key : keySet) {
        if (map.get(key) != null) {
          isEmpty = false;
          break;
        }
      }
    }
    return isEmpty;
  }

  /**
   * Return first value from list
   *
   * @param lstValue specified list
   * @return first element of specified collection, if element is null, return null
   */
  public static Object getFirstValueFromCollection(Collection lstValue) {
    if (!org.apache.commons.collections.CollectionUtils.isEmpty(lstValue)) {
      return lstValue.iterator().next();
    }
    return null;
  }

  /**
   * Safety delete element included in source collection from target collection
   *
   * @param src    source collection
   * @param target target collection
   */
  public static void safeDeleteCollection(List src, List target) {
    int srcSize = src.size();
    for (int i = 0; i < srcSize; i++) {
      Object data2Deleted = src.get(i);
      if (target.contains(data2Deleted)) {
        target.remove(data2Deleted);
        srcSize--;
      }
    }
  }

  /**
   * Sort collection with order, each group is a array list
   *
   * @param source source list
   * @param key    specified key which is used to be compared
   * @return <code>MapListAdapter</code>
   */
  @SuppressWarnings("unchecked")
  public static ListOrderedMap sortCollectionWithOrder(List source, Object key) {
    ListOrderedMap lstOrderedMap = new ListOrderedMap();
    source.stream().forEach(rowMap -> {
      Object keyValue = ObjectUtils.getPropertyValue(rowMap, key.toString());
      List groupList = (List) lstOrderedMap.get(keyValue);
      if (groupList == null) {
        groupList = new ArrayList();
        lstOrderedMap.put(keyValue, groupList);
      }
      groupList.add(rowMap);
    });
    return lstOrderedMap;
  }

  /**
   * Check whether the specified context object has duplicated value of element in collection.
   *
   * @param collection   Collection
   * @param context      Context object which requires to be measured.
   * @param comparedKeys Keys which are used as comparsion keys.
   * @return If it has duplicated value return true, else return false.
   */
  public static boolean checkDuplicatedValue(
      Collection collection,
      Object context, String[] comparedKeys) {
    if (context != null && !org.apache.commons.collections.CollectionUtils.isEmpty(collection)) {
      for (Iterator it = collection.iterator(); it.hasNext(); ) {
        int count = 0;
        Object element = it.next();
        Object[] elementPropertyValueArray = new Object[comparedKeys.length];
        Object[] ctxPropertyValueArray = new Object[comparedKeys.length];
        for (int i = 0; i < comparedKeys.length; i++) {
          if (element instanceof Map) {
            elementPropertyValueArray[i] = ((Map) element).get(comparedKeys[i]);
            ctxPropertyValueArray[i] = ((Map) context).get(comparedKeys[i]);
          } else {
            elementPropertyValueArray[i] = ObjectUtils.getPropertyValue(element, comparedKeys[i]);
            ctxPropertyValueArray[i] = ObjectUtils.getPropertyValue(context, comparedKeys[i]);
          }
        }
        for (int i = 0; i < comparedKeys.length; i++) {
          if (!ObjectUtils.isDifferent(elementPropertyValueArray[i], ctxPropertyValueArray[i])) {
            count += 1;
          }
        }
        if (count == comparedKeys.length) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Sort records in object collection
   *
   * @param objList       Object collection
   * @param propertyName  Property name
   * @param sortAscending Sort indicator
   */
  @SuppressWarnings("unchecked")
  public static void sort(List objList, String propertyName, boolean sortAscending) {
    Comparator c = (o1, o2) -> {
      return (sortAscending ? 1 : -1) *
          ObjectUtils.compareTwoObject(
              ObjectUtils.getPropertyValue(o1, propertyName),
              ObjectUtils.getPropertyValue(o2, propertyName));
    };
    objList.sort(c);
  }
}


