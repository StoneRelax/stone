/*
 * File: $RCSfile: ClassUtilities.java,v $
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

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.tools.meta.NullObj;
import stone.dal.tools.meta.ObjectPropertyDelegate;

import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.Permission;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class <code>ClassUtilities</code>  for class common operations
 *
 * @author feng.xie
 * @version $Revision: 1.45 $
 */
public class ClassUtilities {

    private static Logger s_logger = LoggerFactory.getLogger(ClassUtilities.class);

    public static final SecurityManager SECURITYMANAGER = new SecurityManager() {
        public void checkPermission(Permission perm) {
        }
    };
    public static final SecurityManager SYSSECURITYMANAGER = System.getSecurityManager();
//    private static TraceProducer tracer = Log.tracer(ClassUtilities.class);
    private static Map<String, Class> classTypeMap = new ConcurrentHashMap<String, Class>();
    private static Map<String, Object> propertyAccessorCache = new ConcurrentHashMap<String, Object>();
    private static Map<String, List<Method>> annotationCache = new ConcurrentHashMap<String, List<Method>>();
    //todo: consider performance
    private static Map<String, Method> methodCache = new ConcurrentHashMap<String, Method>();
    private static Map<String, Field> fieldCache = new ConcurrentHashMap<String, Field>();
    private static Map<String, PropertyDescriptor[]> propertyDescCache = new ConcurrentHashMap<String, PropertyDescriptor[]>();

    static {
        classTypeMap.put(String.class.getName(), String.class);
        classTypeMap.put(BigDecimal.class.getName(), BigDecimal.class);
        classTypeMap.put(Double.class.getName(), Double.class);
        classTypeMap.put(Date.class.getName(), Date.class);
        classTypeMap.put(Integer.class.getName(), Integer.class);
        classTypeMap.put(Timestamp.class.getName(), Timestamp.class);
        classTypeMap.put(int.class.getName(), int.class);
        classTypeMap.put(long.class.getName(), long.class);
        classTypeMap.put(Long.class.getName(), Long.class);
        classTypeMap.put(boolean.class.getName(), boolean.class);
        classTypeMap.put(Boolean.class.getName(), Boolean.class);
        classTypeMap.put(Class.class.getName(), Class.class);
        classTypeMap.put(XMLGregorianCalendar.class.getName(), XMLGregorianCalendar.class);
        classTypeMap.put("java.lang.Object", Object.class);
        classTypeMap.put(Byte.class.getName(), Byte.class);
        classTypeMap.put(byte.class.getName(), byte.class);
        classTypeMap.put("string", String.class);
        classTypeMap.put("date", Date.class);
        classTypeMap.put("datetime", Timestamp.class);
        classTypeMap.put("int", Integer.class);
        classTypeMap.put("long", Long.class);
        classTypeMap.put("double", BigDecimal.class);
        classTypeMap.put("boolean", Boolean.class);
        classTypeMap.put("time", String.class);
    }

    /**
     * Constructor
     */
    private ClassUtilities() {

    }

    /**
     * Return boolean representing class type
     *
     * @param clazz Class type
     * @return Boolean flag
     */
    public static boolean isPrimitive(Class clazz) {
        return classTypeMap.containsKey(clazz.getName());
    }

    /**
     * Return is one class implements a class or extends a class
     *
     * @param currClass  current class
     * @param superClass the super class or interface
     * @return the result
     */
    public static boolean isRelationClass(Class currClass, Class superClass) {
        boolean result = false;
        if (currClass != null) {
            if (currClass.equals(superClass)) {
                result = true;
            } else {
                // find in interface
                Class[] interfaces = currClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    Class currInterface = interfaces[i];
                    if (currInterface.equals(superClass)) {
                        result = true;
                    }
                }

                if (!result) {
                    //find in superclass
                    if (currClass.equals(Object.class)) {
                        result = false;
                    } else {
                        List superClasses = ClassUtils.getAllSuperclasses(currClass);
                        if (!CollectionUtils.isEmpty(superClasses)) {
                            Class currSuperClass = (Class) superClasses.get(0);
                            if (currSuperClass == null) {
                                s_logger.warn("Super Class is null");
                                result = false;
                            } else {
                                result = isRelationClass(currSuperClass, superClass);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Import value to object according specified  <code>org.xml.sax.Attributes</code>
     *
     * @param obj          specified object instance
     * @param atts         <code>org.xml.sax.Attributes</code>
     * @param importFields filter fields
     */
    public static void importValueFromAttribute(Object obj, org.xml.sax.Attributes atts, String[] importFields) {
        try {
            for (int i = 0; i < importFields.length; i++) {
                Class propertyType = getPropertyType(obj, importFields[i]);
                String strValue = atts.getValue(importFields[i]);
                String datePattern = atts.getValue("date_pattern");
                Object propertyValue =
                        ObjectUtilities.convertPropertyValueStr2Object(strValue, propertyType, datePattern);
                setPropertyValue(obj, importFields[i], propertyValue);
            }
        } catch (Exception e) {
            s_logger.error(ObjectUtilities.printExceptionStack(e));
        }
    }

    /**
     * Import value to object according specified  <code>org.xml.sax.Attributes</code>
     *
     * @param obj  specified object instance
     * @param atts <code>org.xml.sax.Attributes</code>
     */
    public static void importValueFromAttribute(Object obj, org.xml.sax.Attributes atts) {
        if (atts != null) {
            PropertyDescriptor[] pds;
            try {
                pds = getPropertyDescriptors(obj.getClass());
                if (pds != null && pds.length > 0) {
                    for (int i = 0; i < pds.length; i++) {
                        PropertyDescriptor pd = pds[i];
                        String strValue = atts.getValue(pd.getName());
                        if (strValue != null) {
                            Method setter = pd.getWriteMethod();
                            if (setter != null) {
                                Object value = ConvertUtils.convert(strValue, pd.getPropertyType());
                                Object[] params = {value};
                                setter.invoke(obj, params);
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
     * Return property type of a special property
     *
     * @param obj          Object instance
     * @param propertyName Name of property
     * @return Property clazz
     */
    public static Class getPropertyType(Object obj, String propertyName) {
        Class clazz = null;
        try {

                String _pName = StringUtilities.recoverIllegalPropertyName(propertyName);
                Method readMethod = getAccessor(obj.getClass(), _pName, "read");
                if (readMethod != null) {
                    clazz = readMethod.getReturnType();
                } else {
                    Method writeMethod = getAccessor(obj.getClass(), _pName, "write");
                    if (writeMethod != null) {
                        clazz = writeMethod.getParameterTypes()[0];
                    }
                }
        } catch (Exception e) {
            s_logger.error("Can not find property! [" + obj.getClass().getName() + "." + propertyName + "]");
        }
        return clazz;
    }

    /**
     * Return property type of a special property
     *
     * @param objClass     Class of object
     * @param propertyName Name of property
     * @return Property clazz
     */
    public static Class getPropertyType(Class objClass, String propertyName) {
        Class clazz = null;
        try {
            String _pName = StringUtilities.recoverIllegalPropertyName(propertyName);
            Method writeMethod = getAccessor(objClass, _pName, "read");
            clazz = writeMethod.getReturnType();
        } catch (Exception e) {
            s_logger.error("Can not find property! [" + objClass.getName() + "." + propertyName + "]");
        }
        return clazz;
    }

    /**
     * Export property value of specified object,support "."
     *
     * @param obj          object instance
     * @param propertyName property name
     * @return property value of object
     */
    public static Object getPropertyValue(Object obj, String propertyName, boolean recursive) {
        if (recursive) {
            String[] selectors = StringUtils.split(propertyName, ".");
            if (selectors.length > 0) {
                Object _tmp = obj;
                for (String selector : selectors) {
                    _tmp = getPropertyValue(_tmp, selector);
                }
                return _tmp;
            } else {
                return getPropertyValue(obj, propertyName);
            }
        } else {
            return getPropertyValue(obj, propertyName);
        }
    }

    /**
     * Export property value of specified object
     *
     * @param obj          object instance
     * @param propertyName property name
     * @return property value of object
     */
    public static Object getPropertyValue(Object obj, String propertyName) {
        Object result = null;
        if (propertyName != null) {
            if (obj instanceof Map) {
                result = ((Map) obj).get(propertyName);
            } else {
                try {
                    Method accessor = getAccessor(obj.getClass(), propertyName, "read");
                    if (accessor != null) {
                        result = accessor.invoke(obj);
                    } else {
                        if (obj instanceof ObjectPropertyDelegate) {
                            result = ((ObjectPropertyDelegate) obj).fetchPropertyValue(propertyName);
                        }
                    }
                } catch (Exception e) {
                    s_logger.error("Get Property value error:[" + obj.getClass() + "." + propertyName + "]");
                }
            }
        }
        return result;
    }

    /**
     * Return properties descriptors
     *
     * @param objClazz Object class
     * @return Property descriptor array
     * @throws IntrospectionException Bean exception
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class objClazz) throws IntrospectionException {
        Class _objClazz = getCanonicalClazz(objClazz);
        PropertyDescriptor[] props = propertyDescCache.get(_objClazz.getName());
        if (props == null) {
            BeanInfo info = Introspector.getBeanInfo(_objClazz);
            props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getName().equals("class")
                        || props[i] instanceof IndexedPropertyDescriptor) {
                    props = (PropertyDescriptor[]) ArrayUtils.removeElement(props, props[i]);
                    break;
                }
            }
            propertyDescCache.put(_objClazz.getName(), props);
        }
        return props;
    }

    public static void setPropertyOfEnhancer(
            Object obj,
            String propertyName, Object value) {
        try {
            Method writeMethod = getAccessorOfEnhancer(obj, propertyName, "write");
            if (writeMethod != null) {
                writeMethod.invoke(obj, value);
            }
        } catch (Exception e) {
            s_logger.info("Set property value failure! [" + getCanonicalClazz(obj.getClass()) + "." + propertyName + "]");
        }
    }

    public static Object getPropertyValueOfEnhancer(
            Object obj,
            String propertyName) {
        try {
            Method readMethod = getAccessorOfEnhancer(obj, propertyName, "read");
            if (readMethod != null) {
                return readMethod.invoke(obj);
            }
        } catch (Exception e) {
                s_logger.info(
                        "Get property value failure! [" + getCanonicalClazz(obj.getClass()) + "." + propertyName + "]");
        }
        return null;
    }

    public static Method getAccessorOfEnhancer(
            Object model,
            String propertyName, String methodType)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Class _objClazz = getCanonicalClazz(model.getClass());
        String key = _objClazz.getName() + "." + propertyName + "." + methodType;
        Method accessor = null;
        Object obj;
        if (!propertyAccessorCache.containsKey(key)) {
            PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(model, propertyName);
            if ("write".equals(methodType)) {
                accessor = propertyDescriptor.getWriteMethod();
            } else if ("read".equals(methodType)) {
                accessor = propertyDescriptor.getReadMethod();
            }
            if (accessor != null) {
                obj = accessor;
            } else {
                obj = new NullObj();
            }
            propertyAccessorCache.put(key, obj);
        } else {
            obj = propertyAccessorCache.get(key);
            if (obj instanceof Method) {
                accessor = (Method) obj;
            }
        }
        return accessor;
    }

    /**
     * Return accessor
     *
     * @param objClazz     Class
     * @param propertyName Property name
     * @param methodType   "read","write"
     * @return Method instance
     * @throws IntrospectionException Reflection exception
     */
    public static Method getAccessor(Class objClazz, String propertyName, String methodType)
            throws IntrospectionException {
        Class _objClazz = getCanonicalClazz(objClazz);
        String key = _objClazz.getName() + "." + propertyName + "." + methodType;
        Method accessor = null;
        Object obj;
        if (!propertyAccessorCache.containsKey(key)) {
            PropertyDescriptor[] props = getPropertyDescriptors(_objClazz);
            boolean isFound = false;
            for (int i = 0; i < props.length; i++) {
                PropertyDescriptor prop = props[i];
                String name = prop.getName();
                if (propertyName.equals(name)) {
                    if ("write".equals(methodType)) {
                        accessor = prop.getWriteMethod();
                    } else if ("read".equals(methodType)) {
                        accessor = prop.getReadMethod();
                    }
                    propertyAccessorCache.put(key, accessor);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                propertyAccessorCache.put(key, new NullObj());
            }
        } else {
            obj = propertyAccessorCache.get(key);
            if (obj instanceof Method) {
                accessor = (Method) obj;
            }
        }
        return accessor;
    }


    public static Class getCollectionComponentType(Class clazz, String propertyName) {
        System.setSecurityManager(SECURITYMANAGER);
        try {
            Method readMethod = ClassUtilities.getAccessor(clazz, propertyName, "read");
            Field signatureField = ClassUtilities.getField(Method.class, "signature");
            if (signatureField != null) {
                signatureField.setAccessible(true);
                String signature;
                try {
                    signature = (String) signatureField.get(readMethod);
                } catch (Exception e) {
                    s_logger.warn(
                            "Can not find collection's signature! [" + clazz.getName() + "." + propertyName + "]");
                    return null;
                }
                if (signature != null
                        && signature.indexOf("<") != -1 && signature.indexOf(">") != -1) {
                    signature = StringUtils.replace(signature, "()Ljava/util/List<L", "");
                    signature = StringUtils.replace(signature, "()Ljava/util/Set<L", "");
                    signature = StringUtils.replace(signature, "()Ljava/util/Collection<L", "");
                    signature = StringUtils.replace(signature, ";>;", "");
                    signature = StringUtils.replace(signature, "/", ".");
                    if (SYSSECURITYMANAGER != null) {
                        System.setSecurityManager(SYSSECURITYMANAGER);
                    }
                    return getClazz(signature);
                }
            }
        } catch (Exception e) {
            s_logger.warn(ObjectUtilities.printExceptionStack(e));
        }
        return null;
    }


    /**
     * Return method, the method is cached
     *
     * @param objClazz   Object class
     * @param methodName Method name, forbide use overwrite.
     * @param types      Parameter types
     * @return Method instance
     */
    public static Method getMethod(Class objClazz, String methodName, Class[] types) {
        Class _objClazz = getCanonicalClazz(objClazz);
        String key = _objClazz.getName() + "." + methodName;
        if (!ArrayUtils.isEmpty(types)) {
            for (Class type : types) {
                key += type.getCanonicalName() + ";";
            }
        }
        Method method = methodCache.get(key);
        if (!methodCache.containsKey(key)) {
            try {
                method = _objClazz.getMethod(methodName, types);
            } catch (NoSuchMethodException e) {
            }
            methodCache.put(key, method);
        }
        return method;
    }

    /**
     * Return method, the method is cached
     *
     * @param objClazz   Object class
     * @param methodName Method name, forbide use overwrite.
     * @return Method instance
     */
    public static Method getMethodByArbitraryName(Class objClazz, String methodName) {
        Class _objClazz = getCanonicalClazz(objClazz);
        String key = _objClazz.getName() + "." + methodName;
        Method method = methodCache.get(key);
        if (!methodCache.containsKey(key)) {
            Method[] methods = _objClazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    method = methods[i];
                    break;
                }
            }
            if (method != null) {
                methodCache.put(key, method);
            }
        }
        return method;
    }

    /**
     * Import property value of specified object
     *
     * @param obj           object instance
     * @param propertyName  property name
     * @param propertyValue value of object
     */
    public static void setPropertyValue(Object obj, String propertyName, Object propertyValue) {
        if (propertyName != null) {
            Method writeMethod = null;
            try {
                String _pName = StringUtilities.recoverIllegalPropertyName(propertyName);
                if (obj instanceof Map) {
                    ((Map) obj).put(propertyName, propertyValue);
                } else if (obj instanceof ObjectPropertyDelegate) {
                    ((ObjectPropertyDelegate) obj).importPropertyValue(propertyName, propertyValue);
                } else {
                    Class _objClazz = getCanonicalClazz(obj.getClass());
                    writeMethod = getAccessor(_objClazz, _pName, "write");
                    if (writeMethod != null) {
                        Class pType = writeMethod.getParameterTypes()[0];
                        if (propertyValue != null) {
                            if (propertyValue instanceof Double) {
                                if (pType == Long.class || pType == long.class) {
                                    propertyValue = ((Double) propertyValue).longValue();
                                } else if (pType == int.class || pType == Integer.class) {
                                    propertyValue = ((Double) propertyValue).intValue();
                                }
                            }
                        }
                        writeMethod
                                .invoke(obj, getDefaultPrimitiveValue(writeMethod.getParameterTypes()[0], propertyValue));
                    } else if (obj.getClass().getName().contains("$")) {
                        writeMethod = getAccessorOfEnhancer(obj, propertyName, "write");
                        if (writeMethod != null) {
                            writeMethod.invoke(
                                    obj, getDefaultPrimitiveValue(writeMethod.getParameterTypes()[0], propertyValue));
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    if (writeMethod == null && obj.getClass().getName().contains("$")) {
                        writeMethod = getAccessorOfEnhancer(obj, propertyName, "write");
                    }
                    if (writeMethod != null) {
                        if (propertyValue instanceof Date
                                || propertyValue instanceof Calendar
                                || propertyValue instanceof XMLGregorianCalendar) {
                            Class paramType = writeMethod.getParameterTypes()[0];
                            Object _newValue = DateUtilities.convertDateValue(paramType, propertyValue);
                            try {
                                writeMethod.invoke(obj, _newValue);
                            } catch (Exception e1) {
                                s_logger.error(
                                                "Set property value failure! [" + obj.getClass() + "." + propertyName + "]");
                            }
                        } else if (propertyValue instanceof BigDecimal
                                || propertyValue instanceof Integer
                                || propertyValue instanceof Long) {
                            Class paramType = writeMethod.getParameterTypes()[0];
                            Object _newValue = null;
                            if (paramType == BigDecimal.class) {
                                _newValue = new BigDecimal(propertyValue.toString());
                            } else if (paramType == Integer.class || paramType == int.class) {
                                _newValue = new Integer(propertyValue.toString());
                            } else if (paramType == Long.class || paramType == long.class) {
                                _newValue = new Long(propertyValue.toString());
                            } else if (paramType == Boolean.class || paramType == boolean.class) {
                                if ("1".equals(propertyValue.toString())) {
                                    _newValue = true;
                                } else if ("0".equals(propertyValue.toString())) {
                                    _newValue = false;
                                } else {
                                    s_logger.error(
                                            "Set property value failure! [" + obj.getClass() + "." + propertyName + "]");
                                }
                            }
                            writeMethod.invoke(obj, _newValue);
                        }
                    }
                    return;
                } catch (Exception e1) {
                }
                s_logger.error("Set property value failure! [" + obj.getClass() + "." + propertyName + "]");
            }
        }
    }

    public static Object getDefaultPrimitiveValue(Class type, Object value) {
        if (value == null) {
            if (type == int.class) {
                return 0;
            }
        }
        return value;
    }

    /**
     * Return class with specified type
     *
     * @param type Class type
     * @return Class
     * @throws ClassNotFoundException Exception occurs when class is not found.
     */
    public static Class getClazz(String type) throws ClassNotFoundException {
        Class clazz = classTypeMap.get(type);
        if (clazz == null) {
            clazz = Class.forName(type);
        }
        return clazz;
    }

    /**
     * compare property of two object
     *
     * @param obj1         object instance
     * @param obj2         object instance
     * @param propertyName name of property which is required to be compared
     * @return boolean value
     */
    public static boolean comparePropertyEqual(Object obj1, Object obj2, String propertyName) {
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
     * Return primitive class by specified class name
     * This method has better performance than java reflection.
     *
     * @param className class name
     * @return Primitive class
     */
    public static Class getPrimitiveClass(String className) {
        return classTypeMap.get(className);
    }

    /**
     * Return property names array
     *
     * @param clazz Class
     * @return Property names array
     */
    public static String[] getPropertyNames(Class clazz) {
        try {
            PropertyDescriptor[] propertyDescs = getPropertyDescriptors(clazz);
            String[] propertyNames = new String[propertyDescs.length];
            for (int i = 0; i < propertyDescs.length; i++) {
                propertyNames[i] = propertyDescs[i].getName();
            }
            return propertyNames;
        } catch (IntrospectionException e) {

            s_logger.error(ObjectUtilities.printExceptionStack(e));
        }
        return null;
    }

    /**
     * Return cannoical clazz, drop enhancer.
     *
     * @param clazz Clazz
     * @return POJO class
     */
    public static Class getCanonicalClazz(Class clazz) {
        Class _clazz = clazz;
        if (clazz.getName().indexOf("$") != -1) {
            _clazz = clazz.getSuperclass();
        }
        if (_clazz.getName().indexOf("$") != -1) {
            _clazz = _clazz.getSuperclass();
        }
        return _clazz;
    }

    /**
     * Return field of specified class
     *
     * @param clazz     Specified class
     * @param fieldName Field Name
     * @return Field
     * @throws NoSuchFieldException Exception
     */
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        Class canonicalClazz = getCanonicalClazz(clazz);
        Field field = fieldCache.get(canonicalClazz.getName() + "." + fieldName);
        if (field == null) {
            while (true) {
                if (canonicalClazz != null) {
                    field = canonicalClazz.getDeclaredField(fieldName);
                    if (field != null) {
                        fieldCache.put(canonicalClazz.getName() + "." + fieldName, field);
                        break;
                    }
                } else {
                    break;
                }
                canonicalClazz = canonicalClazz.getSuperclass();
            }
        }
        return field;
    }

    /**
     * Return flag determining whether property is not a collection.
     *
     * @param propertyDescriptor Property descriptor
     * @return Collection indicator
     */
    public static boolean isCollection(PropertyDescriptor propertyDescriptor) {
        Class propertyType = propertyDescriptor.getPropertyType();
        return (
                propertyType.isArray()
                        || ClassUtilities.isRelationClass(propertyType, Collection.class));
    }

    /**
     * Return type of non primitive class
     *
     * @param domainClazz Domain class
     * @param name        Property name
     * @return Class of non primitive property
     */
    public static Class getNonPrimitive(Class domainClazz, String name) {
        Class propertyType = getPropertyType(domainClazz, name);
        if (propertyType.isArray()) {
            propertyType = propertyType.getComponentType();
        } else if (ClassUtilities.isRelationClass(propertyType, Collection.class)) {
            propertyType =
                    ClassUtilities.getCollectionComponentType(domainClazz, name);
        }
        return propertyType;
    }


    public static boolean checkMethodInheritance(Method method, Set<Method> methodSet) {
        if (method == null) {
            return false;
        }

        if (CollectionUtils.isEmpty(methodSet)) {
            return true;
        }

        String name = method.getName();
        int modifiers = method.getModifiers();
        Class return_type = method.getReturnType();
        Class[] parameterType = method.getParameterTypes() != null ? method.getParameterTypes() : new Class[0];
        for (Method orgMethod : methodSet) {
            String org_name = orgMethod.getName();
            if (!name.equals(org_name)) {
                continue;
            }

            Class org_rtn_type = orgMethod.getReturnType();
            if ((return_type.equals(Void.TYPE) || org_rtn_type.equals(Void.TYPE)) && !return_type.equals(org_rtn_type)) {
                continue;
            }

            Class[] orgParameterType =
                    orgMethod.getParameterTypes() != null ? orgMethod.getParameterTypes() : new Class[0];
            if (parameterType.length != orgParameterType.length) {
                continue;
            }

            int org_mod = orgMethod.getModifiers();
            if (modifiers == 1 && org_mod > 1) {
                if (!"java.lang.Object".equals(org_rtn_type.getName()) && !org_rtn_type.equals(Void.TYPE)) {
                    continue;
                }

                if (!checkParameterType(parameterType, orgParameterType)) {
                    continue;
                }

                methodSet.remove(orgMethod);
                break;
            } else if (org_mod == 1 && modifiers > 1) {
                if (!"java.lang.Object".equals(return_type.getName()) && !return_type.equals(Void.TYPE)) {
                    continue;
                }

                if (!checkParameterType(orgParameterType, parameterType)) {
                    continue;
                }

                return false;
            }

        }
        return true;
    }

    private static boolean checkParameterType(Class[] currentParams, Class[] superParams) {
        int index = currentParams.length;
        if (index == 0) {
            return true;
        }

        boolean isSame = true;
        for (int i = 0; i < index; i++) {
            Class current = currentParams[i];
            Class super_p = superParams[i];

            isSame = current.equals(super_p) || "java.lang.Object".equals(super_p.getName());
            if (!isSame) {
                return false;
            }
        }
        return true;
    }
}
/**
 * History:
 * <p/>
 * $Log: ClassUtilities.java,v $
 * Revision 1.45  2010/03/23 08:29:55  fxie
 * no message
 * <p/>
 * Revision 1.44  2010/02/02 07:00:54  fxie
 * no message
 * <p/>
 * Revision 1.43  2009/12/21 15:35:53  fxie
 * no message
 * <p/>
 * Revision 1.42  2009/12/20 03:00:38  fxie
 * no message
 * <p/>
 * Revision 1.41  2009/11/25 11:40:53  fxie
 * no message
 * <p/>
 * Revision 1.40  2009/10/23 08:38:31  fxie
 * no message
 * <p/>
 * Revision 1.39  2009/10/20 07:33:59  fxie
 * no message
 * <p/>
 * Revision 1.38  2009/10/20 07:19:16  fxie
 * no message
 * <p/>
 * Revision 1.37  2009/10/10 08:33:08  fxie
 * no message
 * <p/>
 * Revision 1.36  2009/10/04 11:22:01  fxie
 * no message
 * <p/>
 * Revision 1.35  2009/10/04 05:17:51  fxie
 * no message
 * <p/>
 * Revision 1.34  2009/09/29 11:05:51  fxie
 * no message
 * <p/>
 * Revision 1.33  2009/09/28 03:52:50  fxie
 * no message
 * <p/>
 * Revision 1.32  2009/09/26 23:47:36  fxie
 * no message
 * <p/>
 * Revision 1.31  2009/09/26 16:12:40  fxie
 * no message
 * <p/>
 * Revision 1.30  2009/09/18 10:15:29  fxie
 * no message
 * <p/>
 * Revision 1.29  2009/09/14 04:07:31  fxie
 * no message
 * <p/>
 * Revision 1.28  2009/09/11 09:22:58  fxie
 * no message
 * <p/>
 * Revision 1.27  2009/09/07 18:21:48  fxie
 * no message
 * <p/>
 * Revision 1.26  2009/09/04 08:41:08  fxie
 * no message
 * <p/>
 * Revision 1.25  2009/09/04 02:33:39  fxie
 * no message
 * <p/>
 * Revision 1.24  2009/09/01 06:44:59  fxie
 * no message
 * <p/>
 * Revision 1.23  2009/08/24 15:48:39  fxie
 * no message
 * <p/>
 * Revision 1.22  2009/08/22 13:25:22  fxie
 * no message
 * <p/>
 * Revision 1.21  2009/08/20 09:05:43  fxie
 * no message
 * <p/>
 * Revision 1.20  2009/08/20 07:53:11  fxie
 * no message
 * <p/>
 * Revision 1.19  2009/08/19 11:06:03  fxie
 * no message
 * <p/>
 * Revision 1.18  2009/08/18 06:43:14  fxie
 * no message
 * <p/>
 * Revision 1.17  2009/08/14 10:20:45  fxie
 * no message
 * <p/>
 * Revision 1.16  2009/08/14 06:57:42  fxie
 * no message
 * <p/>
 * Revision 1.15  2009/08/12 16:31:02  fxie
 * no message
 * <p/>
 * Revision 1.14  2009/08/11 13:21:31  fxie
 * no message
 * <p/>
 * Revision 1.13  2009/08/10 15:47:06  fxie
 * Failed commit: Default (2)
 * <p/>
 * Revision 1.12  2009/08/06 16:01:58  fxie
 * no message
 * <p/>
 * Revision 1.11  2009/07/09 01:01:59  fxie
 * Failed commit: Default (2)
 * <p/>
 * Revision 1.10  2009/06/07 17:15:02  fxie
 * no message
 * <p/>
 * Revision 1.9  2009/05/29 16:58:47  fxie
 * no message
 * <p/>
 * Revision 1.8  2009/03/28 16:54:56  fxie
 * no message
 * <p/>
 * Revision 1.7  2009/03/12 13:01:49  fxie
 * no message
 * <p/>
 * Revision 1.6  2009/03/11 12:44:40  fxie
 * no message
 * <p/>
 * Revision 1.5  2009/03/08 15:42:00  fxie
 * no message
 * <p/>
 * Revision 1.4  2009/03/01 05:06:39  fxie
 * no message
 * <p/>
 * Revision 1.3  2009/02/24 16:31:35  fxie
 * no message
 * <p/>
 * Revision 1.2  2009/02/24 10:05:25  fxie
 * no message
 * <p/>
 * Revision 1.1  2009/02/24 06:09:46  fxie
 * no message
 * <p/>
 * Revision 1.1  2008/10/17 10:03:26  fxie
 * *** empty log message ***
 * <p/>
 * Revision 1.13  2008/08/11 12:28:44  fxie
 * add new component
 * <p/>
 * Revision 1.12  2008/07/31 15:03:27  fxie
 * add new component
 * <p/>
 * Revision 1.11  2008/06/06 14:24:14  fxie
 * add new component
 * <p/>
 * Revision 1.10  2008/05/15 16:18:22  fxie
 * add new component
 * <p/>
 * Revision 1.9  2008/03/24 15:39:20  fxie
 * add data source executor
 * <p/>
 * Revision 1.8  2008/02/17 14:19:47  fxie
 * add data source executor
 * <p/>
 * Revision 1.7  2008/01/26 04:33:41  fxie
 * adapt property editor context
 * <p/>
 * Revision 1.6  2008/01/11 12:24:13  fxie
 * add functions in designer
 * <p/>
 * Revision 1.5  2007/09/05 10:47:08  fxie
 * add orm meta
 * <p/>
 * Revision 1.4  2007/08/21 13:50:39  fxie
 * add combobox focus and validation handler
 * <p/>
 * Revision 1.3  2007/08/02 14:36:00  fxie
 * no message
 * <p/>
 * Revision 1.2  2007/07/21 09:56:35  fxie
 * Failed commit: Default (2)
 * <p/>
 * Revision 1.1  2007/06/30 02:21:25  fxie
 * Failed commit: Default (2)
 */


