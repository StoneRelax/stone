package stone.dal.kernel;

import java.util.Comparator;
import java.util.Date;

/**
 * Title: DateComparator
 * Description: DateComparator
 * Copyright: Copyright(c) 2012
 * <p>Company: Dr0ne Studio</p>
 *
 * @author smh
 */
public class DateComparator implements Comparator<String> {
  private String format;

  public DateComparator(String format) {
    this.format = format;
  }

  public int compare(String s1, String s2) {
    Date d1 = ConvertUtils.str2Date(s1, format);
    if (d1 == null) {
      d1 = new Date();
    }
    Date d2 = ConvertUtils.str2Date(s2, format);
    if (d2 == null) {
      d2 = new Date();
    }
    return d1.compareTo(d2);
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
