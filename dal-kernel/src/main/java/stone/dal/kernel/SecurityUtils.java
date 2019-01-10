/*
 * File: $RCSfile: SecurityUtilities.java,v $
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security utilities class
 *
 * @author feng.xie
 * @version $Revision: 1.3 $
 */
public class SecurityUtils {

  private static Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

  /**
   * private constructor
   */
  private SecurityUtils() {
  }

  /**
   * Encrypt data in md5 for string value
   *
   * @param strData source data to be encrypted
   * @return String encrypted string
   */
  public static String md5(String strData) {
    strData = strData.trim();
    String digest = "";
    try {
      MessageDigest currentAlgorithm = MessageDigest.getInstance("md5");
      currentAlgorithm.reset();
      byte[] mess = strData.getBytes();
      byte[] hash = currentAlgorithm.digest(mess);
      for (byte aHash : hash) {
        int v = aHash;
        if (v < 0) {
          v = 256 + v;
        }
        if (v < 16) {
          digest += "0";
        }
        digest += Integer.toString(v, 16).toUpperCase() + "";
      }
    } catch (NoSuchAlgorithmException e) {
      logger.error("MD5 Arithmetic Can Not Be Used Error!");
    }
    return digest;
  }

  /**
   * Encrypt data in md5 for byte[] value
   *
   * @param strData source data to be encrypted
   * @return String encrypted string
   */
  public static String md5(byte[] strData) {
    String digest = "";
    try {
      MessageDigest currentAlgorithm = MessageDigest.getInstance("md5");
      currentAlgorithm.reset();
      byte[] hash = currentAlgorithm.digest(strData);
      for (byte aHash : hash) {
        int v = aHash;
        if (v < 0) {
          v = 256 + v;
        }
        if (v < 16) {
          digest += "0";
        }
        digest += Integer.toString(v, 16).toUpperCase() + "";
      }
    } catch (NoSuchAlgorithmException e) {
      logger.error("MD5 Arithmetic Can Not Be Used Error!");
    }
    return digest;
  }
}