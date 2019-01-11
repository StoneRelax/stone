/*
 * File: $RCSfile: ObjectUtilities.java,v $
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

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Object utilities class, includes several methods responsible for reducing duplicated code for
 * common logic, such comparison between two objects and getting boolean value from string object, etc.
 *
 * @author feng.xie
 * @version $Revision: 1.25 $
 */
public abstract class ObjectUtils {

  /**
   * Compare two object, denote whether these two objects are different , skip null value
   *
   * @param o1 <code>java.lang.Object</code>
   * @param o2 <code>java.lang.Object</code>
   * @return if o1 and o2 have null value, then return true, else, return o1.equals(o2)
   */
  public static boolean isDifferent(Object o1, Object o2) {
    boolean isDifferent = false;
    if (o1 != null ^ o2 != null) {
      isDifferent = true;
      if (o1 != null) {
        if (o1 instanceof String) {
          if (StringUtils.replaceNull(o1).equals(StringUtils.replaceNull(o2))) {
            isDifferent = false;
          }
        }
      } else {
        if (o2 instanceof String) {
          if (StringUtils.replaceNull(o1).equals(StringUtils.replaceNull(o2))) {
            isDifferent = false;
          }
        }
      }
    } else {
      if (o1 != null) {
        if (!o1.equals(o2)) {
          isDifferent = true;
        }
      }
    }
    return isDifferent;
  }

  /**
   * Compare two object approximately. If types of o1 and o2 are string, then
   * if o1 begin with o2, return false.
   *
   * @param o1 <code>java.lang.Object</code>
   * @param o2 <code>java.lang.Object</code>
   * @return boolean value denotes whether two objects are different.
   */
  public static boolean approxDifferent(Object o1, Object o2) {
    boolean isDifferent = false;
    if (o1 != null ^ o2 != null) {
      isDifferent = true;
    } else {
      if (o1 != null) {
        if (o1 instanceof String && o2 instanceof String) {
          String s1 = ((String) o1).trim().toLowerCase();
          String s2 = (((String) o2)).trim().toLowerCase();
          if (!s1.startsWith(s2)) {
            isDifferent = true;
          }
        } else if (!o1.equals(o2)) {
          isDifferent = true;
        }
      }
    }
    return isDifferent;
  }

  /**
   * get boolean value from <code>java.lang.Boolean</code>, skip null value
   *
   * @param value <code>java.lang.Boolean</code>
   * @return if value if null or has false value return false, else return true
   */
  public static boolean unboxBool(Boolean value) {
    return value != null && value;
  }

  /**
   * get boolean value from <code>java.lang.Integer</code>, skip null value
   *
   * @param value <code>java.lang.Integer</code>
   * @return if value if null or its value is 0 return false, else return true
   */
  public static boolean int2bool(Integer value) {
    return value != null && value == 1;
  }

  /**
   * get boolean value from <code>java.lang.String</code>, skip null value
   *
   * @param value <code>java.lang.value</code>
   * @return if value if null or its value is 0 return false, else return true
   */
  public static boolean booleanValueForString(String value) {
    return "true".equals(value) || "1".equals(value);
  }

  /**
   * Return boolean value denotes whether the specified value is null.
   * Additionally, If className is "java.lang.String" and the value is empty, then
   * return true.
   *
   * @param value     Value to be checked
   * @param className Class name of value
   * @return if value is empty return true, else return false.
   */
  public static boolean isNullOrEmpty(Object value, String className) {
    boolean isEmpty = false;
    if ("java.lang.String".equals(className)) {
      if (StringUtils.isEmpty(value)) {
        isEmpty = true;
      }
    } else {
      isEmpty = (value == null);
    }
    return isEmpty;
  }

  /**
   * Return byte array of serializable object
   *
   * @param object serializable object
   * @return <code>byte[]</code>
   * @throws IOException <code>java.io.IOException</code>
   */
  public static byte[] getObjectByteArray(Serializable object) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(object);
    oos.close();
    return bos.toByteArray();
  }

  /**
   * Deserialize byte array to object
   *
   * @param objByteArray <code>bytep[</code>
   * @return <code>java.lang.Object</code>
   * @throws IOException            <code>java.io.IOException</code>
   * @throws ClassNotFoundException <code>java.lang.ClassNotFoundException</code>
   */
  public static Object readObject(byte[] objByteArray) throws IOException, ClassNotFoundException {
    Object result;
    ByteArrayInputStream bis = new ByteArrayInputStream(objByteArray);
    ObjectInputStream is = new ObjectInputStream(bis);
    result = is.readObject();
    is.close();
    return result;
  }

  /**
   * Compare two object
   *
   * @param o1 object 1
   * @param o2 object 2
   * @return {@link Comparable#compareTo(Object)}
   */
  @SuppressWarnings("unchecked")
  public static int compareTwoObject(Object o1, Object o2) {
    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return -1;
    } else if (o2 == null) {
      return 1;
    } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
      int result = ((Comparable) o1).compareTo(o2);
      return normalizeCompareResult(result);
    } else if (o1.getClass().isAssignableFrom(Number.class) && o2.getClass().isAssignableFrom(Number.class)) {
      Number n1 = (Number) o1;
      double d1 = n1.doubleValue();
      Number n2 = (Number) o2;
      double d2 = n2.doubleValue();
      if (d1 < d2) {
        return -1;
      } else if (d1 > d2) {
        return 1;
      } else {
        return 0;
      }
    } else {
      String s1 = o1.toString();
      String s2 = o2.toString();
      int result = s1.compareTo(s2);
      return normalizeCompareResult(result);
    }
  }

  private static int normalizeCompareResult(int compareResult) {
    if (compareResult < 0) {
      return -1;
    } else if (compareResult > 0) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Clone source object as a new object and clone plain properties as well
   *
   * @param sourceObj Source object
   * @return New object
   */
  public static Object flattenCopy(Object sourceObj) {
//		Class clazz = BeanUtils.getCanonicalClazz(sourceObj.getClass());
//		Object newObj = null;
//		try {
//			newObj = clazz.newInstance();
//			ObjectUtils.flattenCopy(sourceObj, newObj);
//		} catch (InstantiationException | IllegalAccessException e) {
//			tracer.error(LogUtils.printEx(e));
//		}
//		return newObj;
    return null;
  }

  /**
   * Clone plain object, copy property value whose type is only primitive type.
   *
   * @param sourceObj Source object
   * @param destObj   Target object
   */
  public static void flattenCopy(Object sourceObj, Object destObj) {
    flattenClone(sourceObj, destObj, false);
  }

  /**
   * Clone plain object, copy property value whose type is only primitive type.
   *
   * @param sourceObj Source object
   * @param destObj   Target object
   */
  public static void flattenClone(Object sourceObj, Object destObj, boolean excludeNullValue) {
    flattenClone(sourceObj, destObj, excludeNullValue, false);
  }

  /**
   * Clone plain object, copy property value whose type is only primitive type.
   *
   * @param sourceObj Source object
   * @param destObj   Target object
   */
  public static void flattenClone(
      Object sourceObj, Object destObj,
      boolean excludeNullValue, List<String> fields, boolean destRef, boolean include) {
//		try {
//			PropertyDescriptor[] descriptors = null;
//			if (destRef) {
//				descriptors = BeanUtils.getPropertyDescriptors(destObj.getClass());
//			} else {
//				descriptors = BeanUtils.getPropertyDescriptors(sourceObj.getClass());
//			}
//			for (PropertyDescriptor descriptor : descriptors) {
//				String propertyName = descriptor.getName();
//				if (fields != null) {
//					boolean contains = fields.contains(propertyName);
//					if (include && !contains) {
//						continue;
//					} else if (!include && contains) {
//						continue;
//					}
//				}
//				boolean isArray = descriptor.getPropertyType().isArray();
//				boolean isPrimitive = BeanUtils.isPrimitive(descriptor.getPropertyType());
//				if (!isArray && isPrimitive) {
//					Object value = BeanUtils.getPropertyValue(sourceObj, propertyName);
//					if (excludeNullValue && value == null) {
//						continue;
//					}
//					BeanUtils.setPropertyValue(
//							destObj, propertyName, value);
//				}
//			}
//		} catch (Exception e) {
//			tracer.error(LogUtils.printEx(e));
//		}
  }

  /**
   * Clone plain object, copy property value whose type is only primitive type.
   *
   * @param sourceObj Source object
   * @param destObj   Target object
   */
  public static void flattenClone(
      Object sourceObj, Object destObj,
      boolean excludeNullValue, boolean destRef) {
    flattenClone(sourceObj, destObj, excludeNullValue, null, destRef, false);
  }

  /**
   * Clone plain object, copy property value whose type is only primitive type.
   *
   * @param sourceObj Source object
   * @param destObj   Target object
   * @param fields    Fields to be handled
   * @param included  If false, <code>fields</code> represents excluding.
   */
  public static void cloneFlattenProperties(Object sourceObj, Object destObj,
      List<String> fields, boolean included) {
//		try {
//			PropertyDescriptor[] desces = BeanUtils.getPropertyDescriptors(sourceObj.getClass());
//			for (int i = 0; i < desces.length; i++) {
//				String propertyName = desces[i].getName();
//				boolean isPrimitive = BeanUtils.isPrimitive(desces[i].getPropertyType());
//				if (isPrimitive) {
//					if (included) {
//						if (fields != null && fields.contains(propertyName)) {
//							BeanUtils.setPropertyValue(
//									destObj, propertyName,
//									BeanUtils.getPropertyValue(sourceObj, propertyName));
//						}
//					} else {
//						if (fields != null && !fields.contains(propertyName)) {
//							BeanUtils.setPropertyValue(
//									destObj, propertyName,
//									BeanUtils.getPropertyValue(sourceObj, propertyName));
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			tracer.error(LogUtils.printEx(e));
//		}
  }

  /**
   * Deep clone object, cloning with nested objects
   *
   * @param source Source object
   * @param target Target object
   */
  public static void copy(Object source, Object target) {
//		try {
//			Class clazz = BeanUtils.getCanonicalClazz(source.getClass());
//			PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
//			for (PropertyDescriptor desc : descriptors) {
//				String propertyName = desc.getName();
//				if (desc.getReadMethod() != null) {
//					boolean noSerialize = desc.getReadMethod().getAnnotation(NoSerialize.class) != null;
//					if (!noSerialize) {
//						boolean isArray = desc.getPropertyType().isArray();
//						boolean isPrimitive = BeanUtils.isPrimitive(desc.getPropertyType());
//						if (isArray) {
//							isPrimitive = BeanUtils.isPrimitive(desc.getPropertyType().getComponentType());
//						}
//						if (BeanUtils.isRelationClass(desc.getPropertyType(), Collection.class)) {
//							isPrimitive = BeanUtils.isPrimitive(
//									BeanUtils.getCollectionComponentType(
//											BeanUtils.getCanonicalClazz(source.getClass()), propertyName));
//						}
//						if (isPrimitive) {
//							BeanUtils.setPropertyValue(
//									target, propertyName,
//									BeanUtils.getPropertyValue(source, propertyName));
//						} else if (isArray) {
//							Object propertyValue = BeanUtils.getPropertyValue(source, propertyName);
//							if (propertyValue != null) {
//								int size = Array.getLength(propertyValue);
//								Object newArray = Array.newInstance(propertyValue.getClass().getComponentType(), size);
//								for (int j = 0; j < size; j++) {
//									Object _childPropertyValue = Array.get(propertyValue, j);
//									Object newChildObj = copyAsNew(_childPropertyValue);
//									Array.set(newArray, j, newChildObj);
//								}
//								BeanUtils.setPropertyValue(target, propertyName, newArray);
//							}
//						} else if (BeanUtils.isRelationClass(desc.getPropertyType(), Collection.class)) {
//							Collection collection = (Collection) BeanUtils.getPropertyValue(source, propertyName);
//							if (collection != null) {
//								Collection newCollection = collection.getClass().newInstance();
//								for (Object _childPropertyValue : collection) {
//									Object newChildObj = copyAsNew(_childPropertyValue);
//									newCollection.add(newChildObj);
//								}
//								BeanUtils.setPropertyValue(target, propertyName, newCollection);
//							}
//						} else {
//							Object childObj = BeanUtils.getPropertyValue(source, propertyName);
//							if (childObj != null) {
//								Object newChildObj = copyAsNew(childObj);
//								BeanUtils.setPropertyValue(target, propertyName, newChildObj);
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			tracer.error(e);
//		}
  }

//	private static Object copyAsNew(Object childObj) throws IllegalAccessException, InstantiationException {
//		Object newChildObj = BeanUtils.getCanonicalClazz(childObj.getClass()).newInstance();
//		copy(childObj, newChildObj);
//		return newChildObj;
//	}

  /**
   * Export property value of specified object,support "."
   *
   * @param obj          object instance
   * @param propertyName property name
   * @return property value of object
   */
  @SuppressWarnings("unchecked")
  public static <T> T getPropertyValue(Object obj, String propertyName) {
    String[] selectors = org.apache.commons.lang.StringUtils.split(propertyName, ".");
    if (selectors.length > 0) {
      Object _tmp = obj;
      for (String selector : selectors) {
        _tmp = getPropertyVal(_tmp, selector);
      }
      return (T) _tmp;
    } else {
      return (T) getPropertyVal(obj, propertyName);
    }
  }

  @SuppressWarnings("unchecked")
  public static void setPropertyValue(Object obj, String propertyName, Object v) {
    String[] selectors = org.apache.commons.lang.StringUtils.split(propertyName, ".");
    try {
      if (selectors.length > 0) {
        Object _tmp = obj;
        Object prev = obj;
        for (int i = 0; i < selectors.length - 1; i++) {
          String selector = selectors[i];
          _tmp = getPropertyValue(_tmp, selector);
          if (_tmp == null) {
            Class propertyType = ClassUtils.getPropertyType(
                org.springframework.util.ClassUtils.getUserClass(prev.getClass()), selector);
            if (propertyType != null) {
              _tmp = propertyType.newInstance();
              setPropertyVal(prev, selector, _tmp);
              prev = _tmp;
            } else {
              throw new KernelRuntimeException(
                  propertyName + " of " + org.springframework.util.ClassUtils.getUserClass(obj.getClass()) +
                      " is invalid!");
            }
          }
        }
        setPropertyVal(_tmp, selectors[selectors.length - 1], v);
      } else {
        setPropertyVal(obj, propertyName, v);
      }
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    }
  }

  /**
   * Export property value of specified object
   *
   * @param obj          object instance
   * @param propertyName property name
   * @return property value of object
   */
  @SuppressWarnings("unchecked")
  private static <T> T getPropertyVal(Object obj, String propertyName) {
    try {
      T result = null;
      if (propertyName != null) {
        if (obj instanceof Map) {
          result = (T) ((Map) obj).get(propertyName);
        } else {
          PropertyDescriptor descriptor = org.springframework.beans.BeanUtils
              .getPropertyDescriptor(obj.getClass(), propertyName);
          if (descriptor != null) {
            Method readMethod = descriptor.getReadMethod();
            result = (T) readMethod.invoke(obj);
          }
        }
      }
      return result;
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    }
  }

  /**
   * Import property value of specified object
   *
   * @param obj           object instance
   * @param propertyName  property name
   * @param propertyValue value of object
   */
  @SuppressWarnings("unchecked")
  private static void setPropertyVal(Object obj, String propertyName, Object propertyValue) throws Exception {
    if (obj != null) {
      if (obj instanceof Map) {
        ((Map) obj).put(propertyName, propertyValue);
      } else {
        PropertyDescriptor descriptor = org.springframework.beans.BeanUtils
            .getPropertyDescriptor(obj.getClass(), propertyName);
        if (descriptor != null) {
          Method writeMethod = descriptor.getWriteMethod();
          if (writeMethod != null) {
            Class pType = writeMethod.getParameterTypes()[0];
            if (propertyValue != null) {
              if (propertyValue instanceof Double) {
                if (pType == Long.class || pType == long.class) {
                  propertyValue = ((Double) propertyValue).longValue();
                } else if (pType == int.class || pType == Integer.class) {
                  propertyValue = ((Double) propertyValue).intValue();
                }
              } else if (propertyValue instanceof Integer && (pType == boolean.class || pType == Boolean.class)) {
                propertyValue = (Integer) propertyValue == 1;
              } else if (propertyValue instanceof Integer && pType.isEnum()) {
                propertyValue = pType.getEnumConstants()[(Integer) propertyValue];
              }
            }
            writeMethod.invoke(obj, getDefaultPrimitiveValue(writeMethod.getParameterTypes()[0], propertyValue));
          }
        }
      }
    }
  }

  private static Object getDefaultPrimitiveValue(Class type, Object value) {
    if (value == null) {
      if (type == int.class) {
        return 0;
      } else if (type == long.class) {
        return 0L;
      }
    }
    return value;
  }

  /**
   * Compare property value of two object
   *
   * @param obj1         object instance
   * @param obj2         object instance
   * @param propertyName name of property which is required to be compared
   * @return boolean value
   */
  public static boolean propertyValueEquals(Object obj1, Object obj2, String propertyName) throws Exception {
    Object value1 = getPropertyValue(obj1, propertyName);
    Object value2 = getPropertyValue(obj2, propertyName);
    boolean isEqual = true;
    if (value1 != null ^ value2 != null) {
      isEqual = false;
    } else {
      if (value1 != null) {
        isEqual = value1.equals(value2);
      }
    }
    return isEqual;
  }

  /**
   * Return value by a given stone.dal.common.api.annotation type
   *
   * @param obj            Object
   * @param annotationType Annotation type
   * @return Value of property
   */
  @SuppressWarnings("unchecked")
  public static <T> T getValueByAnnotation(Object obj, Class annotationType) throws Exception {
    PropertyDescriptor[] descriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(obj.getClass());
    for (PropertyDescriptor prop : descriptors) {
      String name = prop.getName();
      Method readMethod = prop.getReadMethod();
      if (readMethod != null) {
        Annotation[] annotations = readMethod.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
          if (annotationType.equals(annotation.annotationType())) {
            return (T) readMethod.invoke(obj, name);
          }
        }
      }
    }
    return null;
  }
}