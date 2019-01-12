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
package stone.dal.tools.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class DateUtilities {

    private static Logger s_logger = LoggerFactory.getLogger(DateUtilities.class);


    /**
     * private constructor
     */
    private DateUtilities() {
    }

    /**
     * Compare expect date with base date adding interval.
     *
     * @param expectDate   Date to compare
     * @param baseDate     Base Date
     * @param interval     Date Interval, such as 1,2,10
     * @param calendarType Calendar type,such as  'Calendar.YEAR','Calendar.DAY_OF_YEAR'
     * @return True if expectdate>=(basedate + interval); false otherwise
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
     * @param begindate begin date
     * @param enddate   end date
     * @return begindate > endate as false
     */
    public static boolean isEqualYMD(Date begindate, Date enddate) {
        boolean isEquals = true;
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(begindate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(enddate);
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
     * @param begindate begin date
     * @param enddate   end date
     * @return begindate > endate as false
     */
    public static boolean compareYMD(Date begindate, Date enddate) {
        boolean isBefore = true;
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(begindate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(enddate);
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
            } else if (propertyValue instanceof XMLGregorianCalendar) {
                int year = ((XMLGregorianCalendar) propertyValue).getYear();
                int month = ((XMLGregorianCalendar) propertyValue).getMonth();
                int day = ((XMLGregorianCalendar) propertyValue).getDay();
                int hour = ((XMLGregorianCalendar) propertyValue).getHour();
                int min = ((XMLGregorianCalendar) propertyValue).getMinute();
                int sec = ((XMLGregorianCalendar) propertyValue).getSecond();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, min, sec);
                time = calendar.getTimeInMillis();
            }
            if (dateType == Date.class) {
                newDateValue = new Date();
                ((Date) newDateValue).setTime(time);
            } else if (dateType == Timestamp.class) {
                newDateValue = new Timestamp(time);
            } else if (dateType == Calendar.class) {
                newDateValue = Calendar.getInstance();
                ((Calendar) newDateValue).setTime(new Date(time));
            } else if (dateType == XMLGregorianCalendar.class) {
                Calendar _newDateValue = Calendar.getInstance();
                _newDateValue.setTime(new Date(time));
                try {
                    newDateValue = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                            _newDateValue.get(Calendar.YEAR),
                            _newDateValue.get(Calendar.MONTH),
                            _newDateValue.get(Calendar.DATE),
                            _newDateValue.get(Calendar.ZONE_OFFSET) / 60000);
                    ((XMLGregorianCalendar) newDateValue).setHour(_newDateValue.get(Calendar.HOUR));
                    ((XMLGregorianCalendar) newDateValue).setMinute(_newDateValue.get(Calendar.MINUTE));
                    ((XMLGregorianCalendar) newDateValue).setSecond(_newDateValue.get(Calendar.SECOND));
                } catch (DatatypeConfigurationException e) {
                    s_logger.error(ObjectUtilities.printExceptionStack(e));
//                    tracer.logError(ObjectUtilities.printExceptionStack(e));
                }
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
                calendarUnit == Calendar.HOUR_OF_DAY || calendarUnit == Calendar.MINUTE || calendarUnit == Calendar.SECOND || calendarUnit == Calendar.MILLISECOND) {
            cal.setTime(inputDate);
        } else {
            cal.setTime(formatDateWithoutTime(inputDate));
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
    public static Date formatDateWithoutTime(Date inputDate) {
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
    public static void floorDate(Calendar calendar) {
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
     * Parse string to date with specified pattern
     *
     * @param dateVal     string value
     * @param datePattern date pattern
     * @return date converted from @param dateVal with @param datePattern
     */
    public static Date parseDate(String dateVal, String datePattern) {
        Date dateFormatted = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(datePattern);
            dateFormatted = dateFormat.parse(dateVal);
        } catch (ParseException e) {
            try {
                dateFormatted = new Date(new Date().parse(dateVal));
            } catch (Exception ex) {
//                tracer.logError("[DATE FORMAT ERROR:]", ex);
            }
        }
        return dateFormatted;
    }

    /**
     * Parse string to date with default pattern                        yyyy-MM-dd
     *
     * @param dateVal string value
     * @return date converted from @param dateVal with default pattern
     */
    public static Date parseDate(String dateVal) {
        return parseDate(dateVal, "yyyy-MM-dd");
    }

    /**
     * Format date with specified pattern yyyy-MM-dd
     *
     * @param date        specified date
     * @param datePattern specified date pattern
     * @return string of date with @param datePattern
     */
    public static String formatDate(Date date, String datePattern) {
        if (date == null) {
            return "";
        }
        String ret = null;
        try {
            if (StringUtils.isEmpty(datePattern)) {
                datePattern = "yyyy-MM-dd";
            }
            DateFormat dateFormat = new SimpleDateFormat(datePattern);
            ret = dateFormat.format(date);
        } catch (Exception e) {
            s_logger.error("[FORMAT DATE ERROR:]", e);
        }
        return ret;
    }

    /**
     * Format date with default pattern yyyy-MM-dd
     *
     * @param date specified date
     * @return string of date with default pattern
     */
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * Format date with default pattern yyyy-MM-dd
     *
     * @param date specified date
     * @return string of date with default pattern
     */
    public static String formatTimestamp(Timestamp date) {
        return formatDate(date, "yyyy-MM-dd HH:MM:ss");
    }


    /**
     * Check whether specified year is leap year
     *
     * @param _year specified year
     * @return if @param is leap year return true else return false;
     */
    public static boolean isLeapyear(int _year) {
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
                if (isLeapyear(yearOne)) {
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
                if (isLeapyear(yearTwo)) {
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
    public static Integer getDateMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new Integer(c.get(Calendar.MONTH) + 1);
    }

    /**
     * Return year of specified date
     *
     * @param date specified date
     * @return year  value
     */
    public static Integer getDateYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new Integer(c.get(Calendar.YEAR));
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
        return new Integer(c.get(Calendar.DAY_OF_YEAR));
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
    public static List getWeekDay(int weekDay, Date beginDate, Date endDate) {
        int startMonth = getDateMonth(beginDate).intValue();
        int startYear = getDateYear(beginDate).intValue();
        Date startDateMonthFirstDate = getFirstDayOfMonth(startYear, startMonth - 1);
        int firstDayOfStartDate = getWeekDay(startDateMonthFirstDate) - 1;
        int offset = weekDay - firstDayOfStartDate;
        if (offset < 0) {
            offset += 7;
        }
        Date weekStartDate = add(startDateMonthFirstDate, offset, Calendar.DAY_OF_YEAR);
        List lstResult = new ArrayList();
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
    public static boolean beforeTimestamp2Safety(Timestamp ts1, Timestamp ts2) {
        return ts2 != null && ts1.before(ts2);
    }

    /**
     * return current year
     *
     * @return current year value<code>java.lang.Integer</code>
     */
    public static Integer getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        return new Integer(year);
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
        return new Integer(quarter);
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

}
/**
 * History:
 *
 * $Log: DateUtilities.java,v $
 * Revision 1.6  2009/12/21 15:35:43  fxie
 * no message
 *
 * Revision 1.5  2009/12/20 03:00:27  fxie
 * no message
 *
 * Revision 1.4  2009/10/14 08:21:09  fxie
 * no message
 *
 * Revision 1.3  2009/10/12 07:44:03  fxie
 * no message
 *
 * Revision 1.2  2009/09/11 09:22:58  fxie
 * no message
 *
 * Revision 1.1  2009/02/24 06:09:46  fxie
 * no message
 *
 * Revision 1.3  2009/01/11 14:17:42  fxie
 * no message
 *
 * Revision 1.2  2009/01/06 13:00:39  fxie
 * no message
 *
 * Revision 1.1  2008/10/17 10:03:28  fxie
 * *** empty log message ***
 *
 * Revision 1.5  2007/09/04 14:55:07  fxie
 * no message
 *
 * Revision 1.4  2007/08/02 14:36:00  fxie
 * no message
 *
 * Revision 1.3  2007/07/28 15:39:56  fxie
 * no message
 *
 * Revision 1.2  2007/07/21 09:56:35  fxie
 * Failed commit: Default (2)
 *
 * Revision 1.1  2007/06/30 02:21:25  fxie
 * Failed commit: Default (2)
 *
 * Revision 1.1  2007/06/18 11:38:23  fxie
 * no message
 *
 * Revision 1.1  2007/06/16 06:22:22  fxie
 * no message
 *
 * Revision 1.2  2007/06/16 05:47:31  fxie
 * no message
 *
 * Revision 1.1  2007/06/16 04:23:41  fxie
 * no message
 *
 */


