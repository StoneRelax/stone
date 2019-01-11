/*
 * File: $RCSfile: TimezoneUtilities.java,v $
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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time zone utilities class
 *
 * @author feng.xie
 * @version $Revision: 1.3 $
 */
public class TimeZoneUtils {

  /**
   * Server side time zone field
   */
  private static TimeZone defaultTimeZone = TimeZone.getDefault();

  /**
   * private constructor
   */
  private TimeZoneUtils() {
  }

  /**
   * return current UTC date comparision with default timezone
   *
   * @return current UTC date
   */
  public static Date getUTCDate() {
    return getUTCDate(defaultTimeZone, new Date());
  }

  /**
   * return specified UTC date comparision with default timezone
   *
   * @param date specified date
   * @return UTC date corresponding to specified date
   */
  public static Date getUTCDate(Date date) {
    return getUTCDate(defaultTimeZone, date);
  }

  /**
   * return utc date with specified timezone and date
   *
   * @param timeZone specified timezone
   * @param date     specified date
   * @return UTC date corresponding to specified date and timezone
   */
  public static Date getUTCDate(TimeZone timeZone, Date date) {
    int offset = timeZone.getOffset(date.getTime());
    long currUTCTime = date.getTime() - offset;
    return new Date(currUTCTime);
  }

  /**
   * if you want to set server side time zone not as system default, run this method at server start.
   *
   * @param inTimeZone new time zone setting.
   */
  public static void setDefaultTimeZone(TimeZone inTimeZone) {
    defaultTimeZone = inTimeZone;
  }

  /**
   * get the current time at client side.
   *
   * @param inClientTimeZone client time zone.
   * @return client date
   */
  public static Date getClientTime(TimeZone inClientTimeZone) {
    Date serverDate = new Date();
    return convertTime(inClientTimeZone, serverDate);
  }

  /**
   * get the client time
   *
   * @param timeZone client time zone
   * @param calendar server side time
   * @return client side time
   */
  public static Date convertTime(TimeZone timeZone, Calendar calendar) {
    return convertTime(timeZone, calendar.getTime());
  }

  /**
   * get the client time
   *
   * @param inClientTimeZone client time zone
   * @param serverDate       server side time
   * @return client side time
   */
  private static Date convertTime(TimeZone inClientTimeZone, Date serverDate) {
    long serverTime = serverDate.getTime();
    int serverOffset = defaultTimeZone.getOffset(serverTime);
    int clientOffset = inClientTimeZone.getOffset(serverTime);
    long currUTCTime = serverTime - serverOffset;
    long clientTime = currUTCTime + clientOffset;
    if (clientOffset != inClientTimeZone.getOffset(clientTime)) {
      clientTime += inClientTimeZone.getOffset(clientTime) - clientOffset;
    }
    return new Date(clientTime);
  }

  /**
   * Convert  time to time with default timezone
   *
   * @param timeZoneID time zone id
   * @param timestamp  time need to be converted
   * @return time with default timezone
   */
  public static Timestamp convertToServerTime(String timeZoneID, Timestamp timestamp) {
    long clientTimeInMillis = timestamp.getTime();
    int serverOffset = defaultTimeZone.getOffset(clientTimeInMillis);
    int clientOffset = TimeZone.getTimeZone(timeZoneID).getOffset(clientTimeInMillis);
    long currUTCTime = clientTimeInMillis - clientOffset;
    long serverTime = currUTCTime + serverOffset;
    if (serverOffset != defaultTimeZone.getOffset(serverTime)) {
      serverTime = serverTime + defaultTimeZone.getOffset(serverTime) - serverOffset;
    }
    return new Timestamp(serverTime);
  }
}


