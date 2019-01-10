package stone.dal.kernel;

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
}
