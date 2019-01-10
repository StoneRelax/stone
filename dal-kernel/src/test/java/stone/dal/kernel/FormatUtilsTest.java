package stone.dal.kernel;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class FormatUtilsTest {

  @Test
  public void testFormatDecimal() {
    Assert.assertEquals("21.10", FormatUtils.formatDecimal(new BigDecimal((double) 21.1)));
  }
}
