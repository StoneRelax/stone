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
package stone.dal.tools.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Object utilities class, includes several methods responsible for reducing duplicated code for
 * common logic, such comparison between two objects and getting boolean value from string object, etc.
 *
 * @author feng.xie
 * @version $Revision: 1.25 $
 */
public class ObjectUtilities {

    private static Logger s_logger = LoggerFactory.getLogger(DateUtilities.class);

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
                    if (StringUtilities.replaceNull(o1).equals(StringUtilities.replaceNull(o2))) {
                        isDifferent = false;
                    }
                }
            } else {
                if (o2 instanceof String) {
                    if (StringUtilities.replaceNull(o1).equals(StringUtilities.replaceNull(o2))) {
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
    public static boolean booleanValueForBoolean(Boolean value) {
        return value != null && value;
    }

    /**
     * get boolean value from <code>java.lang.Integer</code>, skip null value
     *
     * @param value <code>java.lang.Integer</code>
     * @return if value if null or its value is 0 return false, else return true
     */
    public static boolean booleanValueForInteger(Integer value) {
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
            if (StringUtilities.isEmpty(value)) {
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
        Class objClass;
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        objClass = o1.getClass();
        if (o1 instanceof Comparable && o2 instanceof Comparable) {
            int result = ((Comparable) o1).compareTo(o2);
            return normalizeResult(result);
        } else if (objClass.getSuperclass() == Number.class) {
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
        } else if (objClass == Boolean.class) {
            Boolean bool1 = (Boolean) o1;
            boolean b1 = bool1.booleanValue();
            Boolean bool2 = (Boolean) o2;
            boolean b2 = bool2.booleanValue();
            if (b1 == b2) {
                return 0;
            } else if (b1) {
                return 1;
            } else {
                return -1;
            }
        } else if (objClass == BigDecimal.class) {
            BigDecimal n1 = (BigDecimal) o1;
            double d1 = n1.doubleValue();
            BigDecimal n2 = (BigDecimal) o2;
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
            return normalizeResult(result);
        }
    }

    private static int normalizeResult(int compareResult) {
        if (compareResult < 0) {
            return -1;
        } else if (compareResult > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Conver property string value to object.
     *
     * @param propertyValueStr Property value of string
     * @param clazz            Property class
     * @param datePattern      Date pattern
     * @return Property value
     */
    public static Object convertPropertyValueStr2Object(
            String propertyValueStr,
            Class clazz, String datePattern) {
        Object propertyValue = propertyValueStr;
        if (clazz != null) {
            if (clazz == Timestamp.class) {
                propertyValue = new Timestamp(DateUtilities.parseDate(propertyValueStr, datePattern).getTime());
            } else if (clazz == Date.class) {
                propertyValue = DateUtilities.parseDate(propertyValueStr, datePattern);
            } else if (clazz == Long.class || clazz == long.class) {
                propertyValue = new Long(propertyValueStr);
            } else if (clazz == Integer.class || clazz == int.class) {
                propertyValue = new Integer(propertyValueStr);
            } else if (clazz == BigDecimal.class) {
                propertyValue = new BigDecimal(propertyValueStr);
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                propertyValue = Boolean.valueOf(propertyValueStr);
            }
        }
        return propertyValue;
    }

    /**
     * Convert property string value to object.
     *
     * @param value       Property value of string
     * @param datePattern Date pattern
     * @return Property value
     */
    public static String convertObject2String(Object value, String datePattern) {
        String strValue = null;
        if (value != null) {
            if (value instanceof Timestamp) {
                strValue = DateUtilities.formatTimestamp((Timestamp) value);
            } else if (value instanceof Date) {
                strValue = DateUtilities.formatDate((Date) value, datePattern);
            } else {
                strValue = value.toString();
            }
        }
        return strValue;
    }

    /**
     * Return exception stack of a specified exception
     *
     * @param e Exception
     * @return Exception stack
     */
    public static String printExceptionStack(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.getBuffer().toString();
    }

    /**
     * Clone source object as a new object and clone plain properties as well
     *
     * @param sourceObj Source object
     * @return New object
     */
    public static Object flattenCopy(Object sourceObj) {
        Class clazz = ClassUtilities.getCanonicalClazz(sourceObj.getClass());
        Object newObj = null;
        try {
            newObj = clazz.newInstance();
            ObjectUtilities.flattenCopy(sourceObj, newObj);
        } catch (InstantiationException | IllegalAccessException e) {
            s_logger.error(ObjectUtilities.printExceptionStack(e));
        }
        return newObj;
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
        try {
            PropertyDescriptor[] descriptors = null;
            if (destRef) {
                descriptors = ClassUtilities.getPropertyDescriptors(destObj.getClass());
            } else {
                descriptors = ClassUtilities.getPropertyDescriptors(sourceObj.getClass());
            }
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                if (fields != null) {
                    boolean contains = fields.contains(propertyName);
                    if (include && !contains) {
                        continue;
                    } else if (!include && contains) {
                        continue;
                    }
                }
                boolean isArray = descriptor.getPropertyType().isArray();
                boolean isPrimitive = ClassUtilities.isPrimitive(descriptor.getPropertyType());
                if (!isArray && isPrimitive) {
                    Object value = ClassUtilities.getPropertyValue(sourceObj, propertyName);
                    if (excludeNullValue && value == null) {
                        continue;
                    }
                    ClassUtilities.setPropertyValue(
                            destObj, propertyName, value);
                }
            }
        } catch (Exception e) {
            s_logger.error(ObjectUtilities.printExceptionStack(e));
        }
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
        try {
            PropertyDescriptor[] desces = ClassUtilities.getPropertyDescriptors(sourceObj.getClass());
            for (int i = 0; i < desces.length; i++) {
                String propertyName = desces[i].getName();
                boolean isPrimitive = ClassUtilities.isPrimitive(desces[i].getPropertyType());
                if (isPrimitive) {
                    if (included) {
                        if (fields != null && fields.contains(propertyName)) {
                            ClassUtilities.setPropertyValue(
                                    destObj, propertyName,
                                    ClassUtilities.getPropertyValue(sourceObj, propertyName));
                        }
                    } else {
                        if (fields != null && !fields.contains(propertyName)) {
                            ClassUtilities.setPropertyValue(
                                    destObj, propertyName,
                                    ClassUtilities.getPropertyValue(sourceObj, propertyName));
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_logger.error(ObjectUtilities.printExceptionStack(e));
        }
    }


    private static String getNestedCloneKey(Object obj) {
        return ClassUtilities.getCanonicalClazz(obj.getClass()).getName() + "." + obj.hashCode();
    }

    /**
     * Import data to this object from input Map
     *
     * @param obj    object
     * @param values <code>values</code>
     */
    @SuppressWarnings("unchecked")
    public static void importValueFromMap(Object obj, Map values) {
        if (values != null) {
            PropertyDescriptor[] pds;
            try {
                pds = ClassUtilities.getPropertyDescriptors(obj.getClass());
                if (pds != null && pds.length > 0) {
                    Set keySet = values.keySet();
                    Iterator it = keySet.iterator();
                    while (it.hasNext()) {
                        String name = (String) it.next();
                        String convertedName = StringUtils.replace(name, "_", "");
                        for (PropertyDescriptor pd : pds) {
                            if (pd.getName().equals(name) || pd.getName().equalsIgnoreCase(convertedName)) {
                                Method setter = pd.getWriteMethod();
                                if (setter != null) {
                                    Object v = values.get(name);
                                    if (v != null) {
                                        if (!ClassUtilities.isPrimitive(pd.getPropertyType())) {
                                            if (ClassUtilities.isCollection(pd)) {
                                                Class componentType = ClassUtilities.getCollectionComponentType(obj.getClass(), pd.getName());
                                                Collection collection;
                                                if (pd.getPropertyType().getName().contains("List")) {
                                                    collection = new ArrayList();
                                                } else {
                                                    collection = new HashSet();
                                                }
                                                for (Object o : ((Collection) v)) {
                                                    Object _obj = componentType.newInstance();
                                                    importValueFromMap(_obj, (Map) o);
                                                    collection.add(_obj);
                                                }
                                                v = collection;
                                            } else {
                                                Object _obj;
                                                if (!ClassUtilities.isRelationClass(pd.getPropertyType(), Map.class)) {
                                                    _obj = pd.getPropertyType().newInstance();
                                                    importValueFromMap(_obj, (Map) v);
                                                    v = _obj;
                                                }
                                            }
                                        } else if (pd.getPropertyType() == Integer.class) {
                                            if (v instanceof Double) {
                                                v = ((Double) v).intValue();
                                            }
                                        } else if (pd.getPropertyType() == Long.class) {
                                            if (v instanceof Double) {
                                                v = ((Double) v).longValue();
                                            }
                                            if (v instanceof String){
                                                v = new Long((String) v);
                                            }
                                            if (v instanceof Integer){
                                                v = ((Integer)v).longValue();
                                            }
                                        } else if (pd.getPropertyType() == Boolean.class) {
                                            if (v instanceof Integer) {
                                                v = (((Integer) v) == 1);
                                            }
                                        } else if (pd.getPropertyType() == Timestamp.class && !(v instanceof Timestamp)) {
                                            v = new Timestamp(((Date) v).getTime());
                                        }
                                    }
                                    Object[] params = {v};
                                    try {
                                        setter.invoke(obj, params);
                                    } catch (Exception ex) {
                                        if (v instanceof String) {
                                            Class _type = pd.getReadMethod().getReturnType();
                                            if (_type != String.class) {
                                                v = convertPropertyValueStr2Object((String) v, _type, "yyyy-MM-dd HH:mm:ss");
                                                setter.invoke(obj, v);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                s_logger.error(ObjectUtilities.printExceptionStack(e));
            }
        }
    }

    /**
     * Export property value of specified object to a map
     *
     * @param obj            object instance
     * @param isWithNullable denote whether it is required to skip null value
     * @return map instance includes property value
     */
    public static Map collectAsMap(Object obj, boolean isWithNullable) {
        Map mapOutput = new HashMap();
        PropertyDescriptor[] pds;
        try {
            pds = ClassUtilities.getPropertyDescriptors(obj.getClass());
            for (int i = 0; i < pds.length; i++) {
                PropertyDescriptor pd = pds[i];
                Method getter = pd.getReadMethod();
                Class clazz = pd.getPropertyType();
                if (getter != null
                        && !pd.getName().equals("class")) {
                    Object value = getter.invoke(obj);
                    if (value == null) {
                        if (!isWithNullable) {
                            continue;
                        }
                    }
                    if (ClassUtilities.isPrimitive(clazz)) {
                        if (pd.getPropertyType().equals(String.class)
                                && value == null) {
                            value = "";
                        }
                    } else if (ClassUtilities.isCollection(pd)) {
                        Collection col = (Collection) value;
                        if (col != null) {
                            Collection newCol = new ArrayList();
                            //todo:consider hashset
                            for (Object elm : col) {
                                Map elmMap = collectAsMap(elm, isWithNullable);
                                newCol.add(elmMap);
                            }
                            value = newCol;
                        }
                    } else {
                        if (value != null) {
                            if (!ClassUtilities.isRelationClass(value.getClass(), Map.class)) {
                                value = collectAsMap(value, isWithNullable);
                            }
                        }
                    }
                    mapOutput.put(pd.getName(), value);
                }
            }
        } catch (Exception e) {
            s_logger.error(e.getMessage());
        }
        return mapOutput;
    }

    /**
     * Transform input stream to byte[]
     *
     * @param input Input stream
     * @return Content of input stream
     * @throws Exception Runtime exception
     */
    public static byte[] transformInputstream(InputStream input) throws Exception {
        byte[] byt = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = 0;
        b = input.read();
        while (b != -1) {
            baos.write(b);
            b = input.read();
        }
        byt = baos.toByteArray();
        baos.close();
        return byt;
    }

}
/**
 * History:
 * <p/>
 * $Log: ObjectUtilities.java,v $
 * Revision 1.25  2010/03/17 09:37:57  fxie
 * no message
 * <p/>
 * Revision 1.24  2010/03/15 09:59:53  fxie
 * no message
 * <p/>
 * Revision 1.23  2009/12/21 15:35:53  fxie
 * no message
 * <p/>
 * Revision 1.22  2009/12/20 03:00:38  fxie
 * no message
 * <p/>
 * Revision 1.21  2009/10/20 05:44:26  fxie
 * *** empty log message ***
 * <p/>
 * Revision 1.20  2009/09/29 03:20:10  fxie
 * update
 * <p/>
 * Revision 1.19  2009/09/27 09:42:07  fxie
 * no message
 * <p/>
 * Revision 1.18  2009/09/15 12:47:12  fxie
 * no message
 * <p/>
 * Revision 1.17  2009/09/14 07:37:21  fxie
 * no message
 * <p/>
 * Revision 1.16  2009/09/10 07:31:19  fxie
 * no message
 * <p/>
 * Revision 1.15  2009/09/04 02:33:39  fxie
 * no message
 * <p/>
 * Revision 1.14  2009/09/01 06:44:59  fxie
 * no message
 * <p/>
 * Revision 1.13  2009/08/21 09:27:48  fxie
 * no message
 * <p/>
 * Revision 1.12  2009/08/20 13:04:48  fxie
 * no message
 * <p/>
 * Revision 1.11  2009/08/14 06:57:42  fxie
 * no message
 * <p/>
 * Revision 1.10  2009/08/04 16:13:52  fxie
 * Failed commit: Default (2)
 * <p/>
 * Revision 1.9  2009/07/29 03:09:53  fxie
 * *** empty log message ***
 * <p/>
 * Revision 1.8  2009/05/28 10:20:23  fxie
 * no message
 * <p/>
 * Revision 1.7  2009/05/27 09:36:38  fxie
 * no message
 * <p/>
 * Revision 1.6  2009/03/28 16:54:56  fxie
 * no message
 * <p/>
 * Revision 1.5  2009/03/14 15:33:28  fxie
 * no message
 * <p/>
 * Revision 1.4  2009/03/14 06:28:31  fxie
 * no message
 * <p/>
 * Revision 1.3  2009/03/12 13:01:49  fxie
 * no message
 * <p/>
 * Revision 1.2  2009/03/09 07:10:57  fxie
 * no message
 * <p/>
 * Revision 1.1  2009/02/24 06:09:46  fxie
 * no message
 * <p/>
 * Revision 1.1  2008/10/17 10:03:26  fxie
 * *** empty log message ***
 * <p/>
 * Revision 1.8  2008/08/11 12:28:44  fxie
 * add new component
 * <p/>
 * Revision 1.7  2008/05/17 08:37:01  fxie
 * add new component
 * <p/>
 * Revision 1.6  2008/02/18 12:21:00  fxie
 * add data source executor
 * <p/>
 * Revision 1.5  2007/11/26 15:19:26  fxie
 * add functions in designer
 * <p/>
 * Revision 1.4  2007/09/10 14:02:53  fxie
 * add jdbc query fixture
 * <p/>
 * Revision 1.3  2007/08/02 14:36:00  fxie
 * no message
 * <p/>
 * Revision 1.2  2007/07/28 15:18:00  fxie
 * no message
 * <p/>
 * Revision 1.1  2007/06/30 02:21:25  fxie
 * Failed commit: Default (2)
 * <p/>
 * Revision 1.1  2007/06/18 11:38:23  fxie
 * no message
 * <p/>
 * Revision 1.1  2007/06/16 06:22:22  fxie
 * no message
 * <p/>
 * Revision 1.1  2007/06/16 05:47:52  fxie
 * no message
 */


