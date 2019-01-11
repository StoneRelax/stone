package stone.dal.kernel.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author fengxie
 */
public abstract class UrlUtils {

  public static Set<Class> findClassesByPackage(String packageName) throws ClassNotFoundException, IOException {
    Set<Class> classSet = new HashSet<>();
    Enumeration<URL> urls = UrlUtils.class.getClassLoader().getResources(packageName.replace(".", "/"));
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      if (url != null) {
        String protocol = url.getProtocol();
        if (protocol.equals("file")) {
          String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
          addClass(classSet, packagePath, packageName);
        } else if (protocol.equals("jar")) {
          JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
          if (jarURLConnection != null) {
            JarFile jarFile = jarURLConnection.getJarFile();
            if (jarFile != null) {
              Enumeration<JarEntry> jarEntries = jarFile.entries();
              while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.endsWith(".class")) {
                  String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                      .replaceAll("/", ".");
                  loadClass(classSet, className);
                }
              }

            }
          }
        }
      }
    }
    return classSet;
  }

  private static void addClass(Set<Class> classSet, String packagePath, String packageName)
      throws ClassNotFoundException {
    File[] files = new File(packagePath).listFiles(
        file -> (file.isFile() && file.getName().endsWith(".class") || file.isDirectory()));
    if (files != null) {
      for (File file : files) {
        String fileName = file.getName();
        if (file.isFile()) {
          String className = fileName.substring(0, fileName.lastIndexOf("."));
          if (org.apache.commons.lang.StringUtils.isNotEmpty(packageName)) {
            className = packageName + "." + className;
          }
          loadClass(classSet, className);
        } else {
          String subPackagePath = fileName;
          if (org.apache.commons.lang.StringUtils.isNotEmpty(packagePath)) {
            subPackagePath = packagePath + "/" + subPackagePath;
          }
          String subPackageName = fileName;
          if (org.apache.commons.lang.StringUtils.isNotEmpty(packageName)) {
            subPackageName = packageName + "." + subPackageName;
          }
          addClass(classSet, subPackagePath, subPackageName);
        }
      }
    }
  }

  private static void loadClass(Set<Class> classSet, String className) throws ClassNotFoundException {
    Class<?> cls = Class.forName(className, false, UrlUtils.class.getClassLoader());
    classSet.add(cls);
  }
}
