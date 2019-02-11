/*
 * File: $RCSfile: DateUtilities.java,v $
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Date utilities class
 *
 * @author feng.xie
 * @version $Revision: 1.6 $
 */
public class DateUtils {

  /**
   * private constructor
   */
  private DateUtils() {
  }

  /**
   * Compare expect date with base date adding interval.
   *
   * @param expectDate   Date to compare
   * @param baseDate     Base Date
   * @param interval     Date Interval, such as 1,2,10
   * @param calendarType Calendar type,such as  'Calendar.YEAR','Calendar.DAY_OF_YEAR'
   * @return True if expect date>=(basedate + interval); false otherwise
   */
  public static boolean compareDate(Date expectDate, Date baseDate, int interval, int calendarType) {
    try {
      Calendar expectCal = Calendar.getInstance();
      expectCal.setTime(expectDate);
      Calendar baseCal = Calendar.getInstance();
      baseCal.setTime(baseDate);
      baseCal.add(calendarType, interval);
      return expectCal.after(baseCal);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Return first work day with given year and month
   *
   * @param year  Year
   * @param month Month value from 0
   * @return First work day
   */
  public static Date getFirstWorkday(int year, int month) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, 1);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SATURDAY) {
      calendar.set(year, month, 3);
    } else if (dayOfWeek == Calendar.SUNDAY) {
      calendar.set(year, month, 2);
    }
    return new Date(calendar.getTimeInMillis());
  }

  /**
   * add by xie compare date only compare year month day
   *
   * @param beginDate begin date
   * @param endDate   end date
   * @return begindate > endate as false
   */
  public static boolean equalsYMD(Date beginDate, Date endDate) {
    boolean isEquals = true;
    Calendar startCalendar = Calendar.getInstance();
    startCalendar.setTime(beginDate);
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(endDate);
    int startYear = startCalendar.get(Calendar.YEAR);
    int startMonth = startCalendar.get(Calendar.MONTH);
    int startDay = startCalendar.get(Calendar.DATE);
    int endYear = endCalendar.get(Calendar.YEAR);
    int endMonth = endCalendar.get(Calendar.MONTH);
    int endDay = endCalendar.get(Calendar.DATE);
    if (startYear != endYear || startMonth != endMonth || startDay != endDay) {
      isEquals = false;
    }
    return isEquals;
  }

  /**
   * add by xie compare date only compare year month day
   *
   * @param date1 begin date
   * @param date2 end date
   * @return begindate > endate as false
   */
  public static boolean compareYMD(Date date1, Date date2) {
    boolean isBefore = true;
    Calendar startCalendar = Calendar.getInstance();
    startCalendar.setTime(date1);
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(date2);
    int startYear = startCalendar.get(Calendar.YEAR);
    int startMonth = startCalendar.get(Calendar.MONTH);
    int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
    int endYear = endCalendar.get(Calendar.YEAR);
    int endMonth = endCalendar.get(Calendar.MONTH);
    int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
    if (startYear > endYear) {
      isBefore = false;
    } else if (startYear == endYear) {
      if (startMonth > endMonth) {
        isBefore = false;
      } else if (startMonth == endMonth) {
        if (startDay > endDay) {
          isBefore = false;
        }
      }
    }
    return isBefore;
  }

  /**
   * Convert date value
   *
   * @param dateType      Date type
   * @param propertyValue Property value
   * @return New date value
   */
  public static Object convertDateValue(Class dateType, Object propertyValue) {
    Object newDateValue = null;
    if (dateType != propertyValue.getClass()) {
      long time = -1;
      if (propertyValue instanceof Date) {
        time = ((Date) propertyValue).getTime();
      } else if (propertyValue instanceof Calendar) {
        time = ((Calendar) propertyValue).getTimeInMillis();
      }
      if (dateType == Date.class) {
        newDateValue = new Date();
        ((Date) newDateValue).setTime(time);
      } else if (dateType == Timestamp.class) {
        newDateValue = new Timestamp(time);
      } else if (dateType == Calendar.class) {
        newDateValue = Calendar.getInstance();
        ((Calendar) newDateValue).setTime(new Date(time));
      }
    }
    return newDateValue;
  }

  /**
   * Add Interval to Date
   *
   * @param inputDate    Date to Add
   * @param interval     Interval to added. Subtract is interval is nagative
   * @param calendarUnit Calendar.Year/Calendar.Month/Calendar.Day/Calendar.Hour/Calendar.MInute
   * @return Date added
   */
  public static Date add(Date inputDate, int interval, int calendarUnit) {
    Calendar cal = Calendar.getInstance();
    if (calendarUnit == Calendar.HOUR ||
        calendarUnit == Calendar.HOUR_OF_DAY || calendarUnit == Calendar.MINUTE || calendarUnit == Calendar.SECOND ||
        calendarUnit == Calendar.MILLISECOND) {
      cal.setTime(inputDate);
    } else {
      cal.setTime(floor(inputDate));
    }
    cal.add(calendarUnit, interval);
    return cal.getTime();
  }

  /**
   * Format Date to make it''s hour/minute/second to 0
   *
   * @param inputDate Date to Format
   * @return Date with Year/Month/Day is same with input one while hour/minute/second is 0
   */
  public static Date floor(Date inputDate) {
    Date outputDate = inputDate;
    Calendar cal = Calendar.getInstance();
    cal.setTime(outputDate);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    outputDate = cal.getTime();
    return outputDate;
  }

  /**
   * clear calendar time fields.
   *
   * @param calendar original calendar
   */
  public static void floorCalendar(Calendar calendar) {
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  /**
   * Set last min of specified calendar
   *
   * @param calendar specified calendar
   */
  public static void lastMinOfDay(Calendar calendar) {
    calendar.set(Calendar.HOUR, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  /**
   * Check whether specified year is leap year
   *
   * @param _year specified year
   * @return if @param is leap year return true else return false;
   */
  public static boolean isLeapYear(int _year) {
    return _year % 4 == 0 && (_year % 100 != 0 || _year % 400 == 0);
  }

  /**
   * Get the interval days of the two date.If return result is less than 0,
   * date _one is before _two; if the return result is greater than 0,
   * date _one is after _two.
   *
   * @param _one begin date
   * @param _two end date
   * @return interval days between begin date and end date
   */
  public static int getInterval(Date _one, Date _two) {
    Calendar one = Calendar.getInstance();
    one.setTime(_one);
    Calendar two = Calendar.getInstance();
    two.setTime(_two);

    int yearOne = one.get(Calendar.YEAR);
    int yearTwo = two.get(Calendar.YEAR);
    int dayOne = one.get(Calendar.DAY_OF_YEAR);
    int dayTwo = two.get(Calendar.DAY_OF_YEAR);

    if (yearOne == yearTwo) {
      return dayOne - dayTwo;
    } else if (yearOne < yearTwo) {
      int yearDays = 0;
      while (yearOne < yearTwo) {
        if (isLeapYear(yearOne)) {
          yearDays += 366;
        } else {
          yearDays += 365;
        }

        yearOne += 1;
      }
      return dayOne - yearDays - dayTwo;
    } else {
      int yearDays = 0;
      while (yearTwo < yearOne) {
        if (isLeapYear(yearTwo)) {
          yearDays += 366;
        } else {
          yearDays += 365;
        }

        yearTwo += 1;
      }
      return dayOne - dayTwo + yearDays;
    }
  }

  /**
   * Return first day of current year
   *
   * @return first day value of year
   */
  public static Date getFirstDayOfCurrentYear() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return cal.getTime();
  }

  /**
   * Return first day of date with specified year and month
   *
   * @param year  specified year
   * @param month specified month
   * @return first day value of year of current date
   */
  public static Date getFirstDayOfMonth(int year, int month) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return cal.getTime();
  }

  /**
   * Return last day of date with specified year and month
   *
   * @param year  specified year
   * @param month specified month
   * @return last day value of year of current date
   */
  public static Date getLastDayOfMonth(int year, int month) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    int lastDay = cal.getActualMaximum(Calendar.DATE);
    cal.set(Calendar.DAY_OF_MONTH, lastDay);
    return cal.getTime();
  }

  /**
   * Return first day of current year
   *
   * @return first day value of year of current date
   */
  public static Date getLastDayOfCurrentYear() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, 11);
    cal.set(Calendar.DAY_OF_MONTH, 31);
    return cal.getTime();
  }

  /**
   * Return first day of specified year
   *
   * @param initVal specified date
   * @return first day value of year of specified date
   */
  public static Date getFirstDayOfYear(Date initVal) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(initVal);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return cal.getTime();
  }

  /**
   * Return last day of  current year
   *
   * @param initVDate specified date
   * @return last day value of year of specified date
   */
  public static Date getLastDayOfYear(Date initVDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(initVDate);
    cal.set(Calendar.MONTH, 11);
    cal.set(Calendar.DAY_OF_MONTH, 31);
    return cal.getTime();
  }

  /**
   * Return month of specified date
   *
   * @param date specified date
   * @return month value
   */
  public static int getDateMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MONTH) + 1;
  }

  /**
   * Return year of specified date
   *
   * @param date specified date
   * @return year  value
   */
  public static int getDateYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR);
  }

  /**
   * Return day of year
   *
   * @param date Date
   * @return Day of year
   */
  public static int getDayOfYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_YEAR);
  }

  /**
   * Return week day of specified date
   *
   * @param date specified date
   * @return week day of @param date
   */
  public static int getWeekDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Return offset years between specified begin date and end date
   *
   * @param fdate begin date
   * @param tdate end date
   * @return offset number
   */
  public static int getYearBetweenTwoDate(Date fdate, Date tdate) {
    Calendar f = Calendar.getInstance();
    f.setTime(fdate);
    Calendar t = Calendar.getInstance();
    t.setTime(tdate);

    return t.get(Calendar.YEAR) - f.get(Calendar.YEAR);
  }

  /**
   * Return dates list with specified week day between specified begin data and end date
   *
   * @param weekDay   week day for exampke, Mon, Tus.....
   * @param beginDate begin date
   * @param endDate   end date
   * @return array includes expected date objects whose week day equals to @param weekDay
   */
  public static List<Date> getWeekDay(int weekDay, Date beginDate, Date endDate) {
    int startMonth = getDateMonth(beginDate);
    int startYear = getDateYear(beginDate);
    Date startDateMonthFirstDate = getFirstDayOfMonth(startYear, startMonth - 1);
    int firstDayOfStartDate = getWeekDay(startDateMonthFirstDate) - 1;
    int offset = weekDay - firstDayOfStartDate;
    if (offset < 0) {
      offset += 7;
    }
    Date weekStartDate = add(startDateMonthFirstDate, offset, Calendar.DAY_OF_YEAR);
    List<Date> lstResult = new ArrayList<>();
    while (compareYMD(weekStartDate, endDate)) {
      lstResult.add(weekStartDate);
      weekStartDate = add(weekStartDate, 7, Calendar.DAY_OF_YEAR);
    }
    return lstResult;
  }

  /**
   * Safety compare two timestamp, skip null value
   *
   * @param ts1 first timestamp
   * @param ts2 second timestamp
   * @return if @param ts1 before @param ts2, return true, else return false
   */
  public static boolean before(Timestamp ts1, Timestamp ts2) {
    return ts2 != null && ts1.before(ts2);
  }

  /**
   * return current year
   *
   * @return current year value<code>java.lang.Integer</code>
   */
  public static int getCurrentYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    return calendar.get(Calendar.YEAR);
  }

  /**
   * return current year
   *
   * @return current year value<code>java.lang.Integer</code>
   */
  public static int getCurrentMonth() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    return calendar.get(Calendar.MONTH) + 1;
  }

  /**
   * return current quarter
   *
   * @return current quarter value<code>java.lang.Integer</code>
   */
  public static Integer getCurrentQuarter() {
    int quarter = 1;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    int month = calendar.get(Calendar.MONTH) + 1;
    if (month > 3 && month <= 6) {
      quarter = 2;
    } else if (month > 6 && month <= 9) {
      quarter = 3;
    } else if (month > 9) {
      quarter = 4;
    }
    return quarter;
  }

  /**
   * Get tomorrow date
   *
   * @param date today date
   * @return tomorrow date
   */
  public static Date getNextDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_YEAR, +1);
    return calendar.getTime();
  }

  public static String getFirstDayOfWeek(Date time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);

    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

    calendar.add(Calendar.DAY_OF_YEAR, -dayOfWeek);

    String monthStr = String.valueOf(calendar.get(Calendar.MONTH) + 1);
    if (monthStr.length() <= 1) {
      monthStr = "0" + monthStr;
    }

    String day = String.valueOf(calendar.get(Calendar.DATE));
    if (day.length() <= 1) {
      day = "0" + day;
    }
    return String.valueOf(calendar.get(Calendar.YEAR)) + monthStr + day;
  }
}

