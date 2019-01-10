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
package stone.dal.kernel;

import java.awt.*;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * The class <code>BeanUtils</code>  for class common operations
 *
 * @author feng.xie
 * @version $Revision: 1.45 $
 */
public abstract class ClassUtils {

  private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);

  /**
   * Return property type of a special property
   *
   * @param obj          Object instance
   * @param propertyName Name of property
   * @return Property clazz
   */
  public static Class getPropertyType(Object obj, String propertyName) {
    if (obj != null) {
      return getPropertyType(obj.getClass(), propertyName);
    }
    return null;
  }

  /**
   * Return property type of a special property
   *
   * @param clazz        Class of object
   * @param propertyName Name of property
   * @return Property clazz
   */
  public static Class getPropertyType(Class clazz, String propertyName) {
    PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(clazz, propertyName);
    if (descriptor != null) {
      return descriptor.getPropertyType();
    }
    return null;
  }

  /**
   * Return write method by a given class and property name
   *
   * @param clazz        Class
   * @param propertyName Property name
   * @return Read method
   */
  public static Method getWriteMethod(Class clazz, String propertyName) {
    PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(
        clazz, propertyName);
    if (descriptor != null) {
      return descriptor.getWriteMethod();
    }
    return null;
  }

  /**
   * Return read method by a given class and property name
   *
   * @param clazz        Class
   * @param propertyName Property name
   * @return Read method
   */
  public static Method getReadMethod(Class clazz, String propertyName) {
    PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(
        clazz, propertyName);
    if (descriptor != null) {
      return descriptor.getReadMethod();
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
    return org.springframework.beans.BeanUtils.findMethod(objClazz, methodName, types);
  }

  /**
   * Return method, the method is cached
   *
   * @param objClazz   Object class
   * @param methodName Method name, forbide use overwrite.
   * @return Method instance
   */
  public static Method getMethodByArbitraryName(Class objClazz, String methodName) {
    return org.springframework.beans.BeanUtils.findMethodWithMinimalParameters(objClazz, methodName);
  }

  /**
   * Return property names array
   *
   * @param clazz Class
   * @return Property names array
   */
  public static Collection<String> getPropertyNames(Class clazz) {
    PropertyDescriptor[] propertyDescriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
    List<String> names = new ArrayList<>();
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      if (!propertyDescriptor.getName().equals("class")) {
        names.add(propertyDescriptor.getName());
      }
    }
    return Collections.unmodifiableCollection(names);
  }

  /**
   * Split class name to be a string array.
   *
   * @param className Class name with full package name
   * @return String[0]=className, String[1]=packageName
   */
  public static String[] getClassFullPath(String className) {
    String[] _segments = StringUtils.splitString2Array(className, ".");
    StringBuilder packageName = new StringBuilder();
    for (int i = 0; i < _segments.length - 1; i++) {
      packageName.append(_segments[i]);
      if (i < _segments.length - 2) {
        packageName.append(".");
      }
    }
    if (org.apache.commons.lang.StringUtils.isEmpty(packageName.toString())) {
      packageName = new StringBuilder("java.lang");
    }
    String _className = _segments[_segments.length - 1];
    String[] path = new String[2];
    path[0] = _className;
    path[1] = packageName.toString();
    return path;
  }

  public static Set<URL> getResources(String packageName, String ext) throws IOException {
    Set<URL> resourceSet = new HashSet<>();
    Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(
        packageName.replace(".", "/"));
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      if (url != null) {
        String protocol = url.getProtocol();
        if (protocol.equals("file")) {
          String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
          File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith("." + ext)));
          if (files != null) {
            for (File file : files) {
              resourceSet.add(file.toURI().toURL());
            }
          }
        } else if (protocol.equals("jar")) {
          JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
          if (jarURLConnection != null) {
            JarFile jarFile = jarURLConnection.getJarFile();
            if (jarFile != null) {
              Enumeration<JarEntry> jarEntries = jarFile.entries();
              while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.endsWith("." + ext)) {
                  String relativePath = jarEntry.getName();
                  if (relativePath.startsWith(jarEntry.getName())) {
                    URL perResource = new URL(url, relativePath.substring(jarEntry.getName().length()));
                    resourceSet.add(perResource);
                  }
                }
              }
            }
          }
        }
      }
    }
    return resourceSet;
  }

  /**
   * Get collection type
   *
   * @param clazz        Class
   * @param propertyName Property name
   * @return Type
   */
  public static Class getCollectionType(Class clazz, String propertyName) throws ClassNotFoundException {
    Assert.notNull(clazz, "Class must not be null");
    Method method = getReadMethod(clazz, propertyName);
    Assert.notNull(method, "Method must not be null");
    ParameterizedType rtnType = (ParameterizedType) method.getGenericReturnType();
    Type type = rtnType.getActualTypeArguments()[0];
    Class pClazz = org.springframework.util.ClassUtils.resolvePrimitiveClassName(type.getTypeName());
    if (pClazz == null) {
      pClazz = org.springframework.util.ClassUtils.forName(type.getTypeName(), ClassUtils.class.getClassLoader());
    }
    return pClazz;
  }

  public static boolean isPrimitive(Class clazz) {
    boolean isPrimitive = org.springframework.util.ClassUtils.isPrimitiveOrWrapper(clazz);
    if (!isPrimitive) {
      isPrimitive = (clazz == String.class || clazz == Date.class
          || clazz == BigDecimal.class || clazz == Timestamp.class || clazz == Color.class);
    }
    return isPrimitive;
  }
}