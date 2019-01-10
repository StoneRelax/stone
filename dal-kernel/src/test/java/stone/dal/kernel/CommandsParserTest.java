package stone.dal.kernel;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fengxie
 */
public class CommandsParserTest {

  @Test
  public void testParseArgs() {
    String args = "--Xss=265m --Xms=512m";
    List<CommandsParser.Argument> arguments = CommandsParser.parseArgs(args);
    Assert.assertEquals("265m", arguments.get(0).getVal());
    Assert.assertEquals("Xss", arguments.get(0).getKey());
    Assert.assertEquals("512m", arguments.get(1).getVal());
    Assert.assertEquals("Xms", arguments.get(1).getKey());
  }

}
