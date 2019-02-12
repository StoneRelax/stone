package stone.dal.kernel.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import org.slf4j.Logger;

/**
 * @author fengxie
 */
public class LogUtils {

  /**
   * Return exception stack of a specified exception
   *
   * @param e Exception
   * @return Exception stack
   */
  public static String printEx(Throwable e) {
    while ((e instanceof UndeclaredThrowableException || e instanceof InvocationTargetException)) {
      e = e.getCause();
    }
    StringBuilder sb = new StringBuilder();
    if (e != null) {
      Throwable throwable = e;
      while (throwable != null) {
        sb.append(throwable.getClass().getName());
        sb.append(":");
        sb.append(throwable.getMessage());
        sb.append("\n");
        throwable = throwable.getCause();
      }
    }
    return sb.toString();
  }

  public static void error(Logger logger, Exception ex) {
    logger.error(printEx(ex));
  }
}
