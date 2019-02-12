/*
 * File: $RCSfile: FileUtilities.java,v $
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File utilities class
 *
 * @author feng.xie
 * @version $Revision: 1.5 $
 */
public class FileUtils {

  private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
  }

  /**
   * delete file with specified file name
   *
   * @param file file name
   * @return if delete success, return true, else return false
   */
  public static boolean deleteFile(String file) {
    File aFile = new File(file);
    return aFile.exists() && aFile.isFile() && aFile.delete();
  }

  /**
   * write file with specified path and content
   *
   * @param filePath file path
   * @param content  file content
   */
  public static void writeFile(String filePath, byte[] content) {
    writeFile(filePath, content, false);
  }

  private static void writeFile(String filePath, byte[] content, boolean append) {
    if (content != null) {
      File infoFile = new File(filePath);
      if (!infoFile.exists()) {
        File dir = infoFile.getParentFile();
        if (dir != null && !dir.exists()) {
          boolean success = dir.mkdirs();
          if (!success) {
            throw new KernelRuntimeException(String.format("Can't create folder (%s)!", filePath));
          }
        }
      }
      try {
        FileOutputStream fos = new FileOutputStream(infoFile, append);
        fos.write(content);
        fos.flush();
        fos.close();
      } catch (IOException e) {
        LogUtils.error(logger, e);
      }
    }
  }

  /**
   * append file with specified path and content
   *
   * @param filePath file path
   * @param content  file content
   */
  public static void appendFile(String filePath, byte[] content) {
    writeFile(filePath, content, true);
  }

  /**
   * Write file with specified path and content
   *
   * @param path file path
   * @param fis  file inputstream
   */
  public static void writeFile(String path, InputStream fis) throws IOException {
    int n = 512;
    byte[] buffer = new byte[512];
    FileOutputStream fos = null;
    try {
      File infoFile = new File(path);
      if (!infoFile.exists()) {
        File dir = infoFile.getParentFile();
        if (dir != null && !dir.exists()) {
          dir.mkdirs();
        }
      }
      fos = new FileOutputStream(path);
      int readLength;
      while (((readLength = fis.read(buffer, 0, n)) != -1) && (n > 0)) {
        fos.write(buffer, 0, readLength);
      }
      fos.flush();
    } finally {
      fis.close();
      if (fos != null) {
        fos.close();
      }
    }
  }

  /**
   * read file to byte[] with specified file path
   *
   * @param filePath specified file path
   * @return <code>byte[]</code> file content
   */
  public static byte[] readFile(String filePath) {
    File infoFile = new File(filePath);
    byte[] result = null;
    if (infoFile.exists()) {
      result = new byte[(int) infoFile.length()];
      try {
        FileInputStream fis = new FileInputStream(infoFile);
        fis.read(result);
        fis.close();
      } catch (IOException e) {
        LogUtils.error(logger, e);
      }
    }
    return result;
  }

  /**
   * make a directory for specified file
   * if directory existed, it is not requried to create a new one
   *
   * @param file specified file<code>java.io.File</code>
   */
  public static void createDir(File file) {
    if (!file.exists()) {
      file.mkdirs();
      if (logger.isInfoEnabled()) {
        logger.info("Create dir:" + file.getAbsolutePath());
      }
    }
  }

  /**
   * create file
   *
   * @param file file
   * @return whether create successfully.
   */
  public static boolean createFile(File file) {
    boolean success = false;
    if (!file.exists()) {
      File dir = file.getParentFile();
      if (dir != null && (!dir.exists() || !dir.isDirectory())) {
        dir.mkdirs();
      }
      try {
        success = file.createNewFile();
      } catch (IOException e) {
        LogUtils.error(logger, e);
      }
    }
    return success;
  }

  /**
   * Copy sourceFolder's content to a new folder
   *
   * @param sourceFolder source folder
   * @param newfolder    destinate folder
   */
  public static void copyFolder(String sourceFolder, String newfolder) {
    copyFolder(sourceFolder, newfolder, true);
  }

  /**
   * Copy source folder's content to a new folder, if there are existed duplicated files and overwrite
   * flag is true, it might overwrite it
   *
   * @param sourceFolder   source folder path
   * @param newFolder      new folder path
   * @param forceOverwrite determine whether it is required to overwrite for duplicated files
   */
  public static void copyFolder(String sourceFolder, String newFolder, boolean forceOverwrite) {
    String oldPath = org.apache.commons.lang.StringUtils.replace(sourceFolder, "/", File.separator);
    String newPath = org.apache.commons.lang.StringUtils.replace(newFolder, "/", File.separator);
    if (!oldPath.endsWith(File.separator)) {
      oldPath = oldPath + File.separator;
    }
    if (!newPath.endsWith(File.separator)) {
      newPath = newPath + File.separator;
    }
    new File(newPath).mkdirs();
    File sourceDir = new File(oldPath);
    String[] files = sourceDir.list();
    File oldFile;
    File newFile;
    for (int i = 0; i < files.length; i++) {
      oldFile = new File(oldPath + files[i]);
      newFile = new File(newPath + files[i]);
      if (oldFile.isFile() && (
          !forceOverwrite || (
              !newFile.exists() || oldFile.length() != newFile.length() || oldFile.lastModified() > newFile
                  .lastModified()))) {
        copyFile(oldFile.getPath(), newFile.getPath());
      }
      if (oldFile.isDirectory()) {
        copyFolder(oldPath + files[i], newPath + files[i], forceOverwrite);
      }
    }
  }

  /**
   * Copy file from source path to a new path
   *
   * @param sourcePath source path
   * @param newPath    new path
   */
  public static void copyFile(String sourcePath, String newPath) {
    byte[] sourceByte = readFile(sourcePath);
    writeFile(newPath, sourceByte);
    if (isExisted(newPath)) {
      logger.info("Copy " + sourcePath + " to " + newPath + " successful!");
    } else {
      logger.info("Copy " + sourcePath + " to " + newPath + " not successful!");
    }
  }

  /**
   * Return boolean value denotes whether the file with specified file path existed
   *
   * @param filePath File path
   * @return Boolean value denotes whether the file with specified file path existed
   */
  public static boolean isExisted(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

  /**
   * Copy file and remove the old one.
   *
   * @param sourcePath {@link #copyFile(String, String)}
   * @param newPath    {@link #copyFile(String, String)}
   */
  public static void cutFile(String sourcePath, String newPath) {
    copyFile(sourcePath, newPath);
    File file = new File(sourcePath);
    if (file.isDirectory()) {
      deleteAll(file);
    } else {
      deleteFile(sourcePath);
    }
  }

  /**
   * visit all a director and delete them in a list,write deleted files' name to list
   *
   * @param dir directory
   * @return deleted file name list
   */
  private static List deleteAll(File dir) {
    List allFiles = new ArrayList();
    File[] dirs = dir.listFiles();
    if (dirs != null) {
      List dirsList = Arrays.asList(dirs);
      allFiles.addAll(dirsList);
      for (Iterator it = dirsList.iterator(); it.hasNext(); ) {
        File _tempRoot = (File) it.next();
        allFiles.addAll(deleteAll(_tempRoot));
      }
    }
    return allFiles;
  }

  /**
   * do delete all files under specified file's dir
   *
   * @param root specified file root
   */
  public static void deleteDirs(File root) {
    if (root.isDirectory()) {
      List allFiles = deleteAll(root);
      if (allFiles != null) {
        for (int i = allFiles.size() - 1; i >= 0; i--) {
          File f = (File) allFiles.remove(i);
          String fileName = f.toString();
          if (!f.delete()) {
            throw new KernelRuntimeException("Exception: delete file " + fileName + " false!");
          }
        }
      }
      root.delete();
    }
  }

  /**
   * return input  stream from specified url
   *
   * @param url specified url
   * @return <code>java.io.InputStream</code>
   */
  public static InputStream getInputStream(String url) {
    InputStream is = null;
    if (url != null) {
      if (url.contains(File.separator)) {
        byte[] infoArray = readFile(url);
        if (infoArray != null) {
          is = new ByteArrayInputStream(infoArray);
        }
      } else {
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(url);
      }
    }
    return is;
  }

  /**
   * Return last modified time of specified file
   *
   * @param fileName file name
   * @return last modified time long value
   */
  public static long getLastModifiedTime(String fileName) {
    try {
      File f = new File(fileName);
      return f.lastModified();
    } catch (Exception ex) {
      return -1;
    }
  }

  /**
   * Return extension file name with specified file name
   *
   * @param fileName File name
   * @return Extension file name
   */
  public static String getExtFileName(String fileName) {
    String extFileName;
    char[] fileCharArray = fileName.toCharArray();
    int breakIndex = fileCharArray.length - 1;
    for (; breakIndex > 0; breakIndex--) {
      if (fileCharArray[breakIndex] == '.') {
        break;
      }
    }
    extFileName = fileName.substring(breakIndex + 1, fileCharArray.length);
    return extFileName;
  }

  /**
   * Replace invalid characters to adapt file system
   *
   * @param fileName Primitive file name
   * @return String valid file name
   */
  public static String validateFileName(String fileName) {
    String validFileName = org.apache.commons.lang.StringUtils.replace(fileName, "\\", "_SC1_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "/", "_SC2_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, ":", "_SC3_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "*", "_SC4_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "?", "_SC5_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "\"", "_SC6_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "<", "_SC7_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, ">", "_SC8_");
    validFileName = org.apache.commons.lang.StringUtils.replace(validFileName, "|", "_SC9_");
    return validFileName;
  }

  public static Set<String> readFilesMd5Set(String srcPath, String packageName, final String[] extFilters) {
    Set<String> md5Set = new HashSet<>();
    File file = new File(srcPath);
    file = new File(file.getAbsoluteFile() + File.separator +
        org.apache.commons.lang.StringUtils.replace(packageName, ".", File.separator));
    if (file.exists()) {
      File[] files = file.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          boolean canAccept = true;
          for (String extFilter : extFilters) {
            if (!pathname.getName().endsWith("." + extFilter)) {
              canAccept = false;
              break;
            }
          }
          return canAccept;
        }
      });
      if (files != null) {
        for (File _file : files) {
          String md5 = SecurityUtils.md5(readFile(_file.getAbsolutePath()));
          md5Set.add(md5);
        }
      }
    }
    return md5Set;
  }
}