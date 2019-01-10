/*
 * File: $RCSfile: FormatUtilities.java,v $
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format utilities class
 *
 * @author feng.xie
 * @version $Revision: 1.3 $
 */
public class FormatUtils {

  private static Logger logger = LoggerFactory.getLogger(FormatUtils.class);

  /**
   * Constructor
   */
  private FormatUtils() {
  }

  /**
   * format str to time str (hh:mm)
   *
   * @param str String to be converted
   * @return String
   */
  public static String formatTimeStr(String str) {
    String result = "";
    if (str != null && str.length() > 0) {
      try {
        result = str.substring(0, 2) + ":" + str.substring(2);
      } catch (Exception e) {
        LogUtils.error(logger, e);
        result = str;
      }
    }
    return result;
  }

  /**
   * format bigdecimal to string
   *
   * @param number bigdecimal converted to string
   * @return <code>String</code>
   */
  public static String formatDecimal(BigDecimal number) {
    String result = "";
    NumberFormat nbf = new DecimalFormat("#,##0.00");
    if (number != null) {
      result = nbf.format(number.doubleValue());
    }
    return result;
  }

  /**
   * format bigdecimal to string with specified pattern
   *
   * @param number  bigdecimal converted to string
   * @param pattern specified pattern
   * @return <code>String</code>
   */
  public static String formatDecimal(BigDecimal number, String pattern) {
    String result = "";
    NumberFormat nbf = new DecimalFormat(pattern);
    if (number != null) {
      result = nbf.format(number.doubleValue());
    }
    return result;
  }

}