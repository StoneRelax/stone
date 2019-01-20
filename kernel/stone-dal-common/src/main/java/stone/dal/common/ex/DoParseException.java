package stone.dal.common.ex;

public class DoParseException extends Exception {
  public DoParseException() {
  }

  public DoParseException(String message) {
    super(message);
  }

  public DoParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public DoParseException(Throwable cause) {
    super(cause);
  }

  public DoParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
