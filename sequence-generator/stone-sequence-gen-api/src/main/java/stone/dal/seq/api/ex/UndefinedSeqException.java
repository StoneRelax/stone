package stone.dal.seq.api.ex;

public class UndefinedSeqException extends Exception {
  public UndefinedSeqException() {
  }

  public UndefinedSeqException(String message) {
    super(message);
  }

  public UndefinedSeqException(String message, Throwable cause) {
    super(message, cause);
  }

  public UndefinedSeqException(Throwable cause) {
    super(cause);
  }

  public UndefinedSeqException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
