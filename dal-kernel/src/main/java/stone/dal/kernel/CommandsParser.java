package stone.dal.kernel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengxie
 */
public class CommandsParser {

  /**
   * Parse argument string
   * e.g. --key1=value1 --key2=value2 --key3=value3
   *
   * @param args Arguments
   * @return Argument objects
   */
  public static List<Argument> parseArgs(String args) {
    List<String> argPairs = StringUtils.splitString(args, "--");
    List<Argument> arguments = new ArrayList<>();
    argPairs.forEach(argPair -> {
      String[] argStrs = StringUtils.splitString2Array(argPair, "=");
      arguments.add(new Argument(argStrs[0], argStrs[1]));
    });
    return arguments;
  }

  public static class Argument {
    private String key;

    private String val;

    public Argument(String key, String val) {
      this.key = key;
      this.val = val;
    }

    public String getKey() {
      return key;
    }

    public String getVal() {
      return val;
    }
  }
}
