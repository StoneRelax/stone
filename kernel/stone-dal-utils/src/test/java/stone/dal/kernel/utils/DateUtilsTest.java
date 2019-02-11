package stone.dal.kernel.utils;

import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class DateUtilsTest {

  @Test
  public void testCompareYMD() {
    Date date1 = ConvertUtils.str2Date("2015-01-01");
    Date date2 = ConvertUtils.str2Date("2015-01-01");
    Date date3 = ConvertUtils.str2Date("2015-02-01");
    Assert.assertTrue(DateUtils.compareYMD(date1, date2));
    Assert.assertTrue(DateUtils.compareYMD(date1, date3));
    Assert.assertFalse(DateUtils.compareYMD(date3, date1));
  }

  @Test
  public void testGetFirstDayOfWeek(){
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2018);
    calendar.set(Calendar.MONTH, 11);
    calendar.set(Calendar.DATE, 30);
    Assert.assertEquals("20181230", DateUtils.getFirstDayOfWeek(new Date(calendar.getTimeInMillis())));
    calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2018);
    calendar.set(Calendar.MONTH, 11);
    calendar.set(Calendar.DATE, 29);
    Assert.assertEquals("20181223", DateUtils.getFirstDayOfWeek(new Date(calendar.getTimeInMillis())));
    calendar.set(Calendar.DATE, 27);
    Assert.assertEquals("20181223", DateUtils.getFirstDayOfWeek(new Date(calendar.getTimeInMillis())));
    calendar.set(Calendar.DATE, 24);
    Assert.assertEquals("20181223", DateUtils.getFirstDayOfWeek(new Date(calendar.getTimeInMillis())));
    calendar.set(Calendar.DATE, 23);
    Assert.assertEquals("20181223", DateUtils.getFirstDayOfWeek(new Date(calendar.getTimeInMillis())));
  }
}
