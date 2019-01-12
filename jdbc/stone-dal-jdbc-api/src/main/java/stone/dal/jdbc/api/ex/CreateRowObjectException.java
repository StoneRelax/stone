package stone.dal.jdbc.api.ex;

public class CreateRowObjectException extends Exception {

  public CreateRowObjectException() {
  }

  public CreateRowObjectException(String message) {
    super(message);
  }

  public CreateRowObjectException(String message, Throwable cause) {
    super(message, cause);
  }

  public CreateRowObjectException(Throwable cause) {
    super(cause);
  }

  public CreateRowObjectException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
