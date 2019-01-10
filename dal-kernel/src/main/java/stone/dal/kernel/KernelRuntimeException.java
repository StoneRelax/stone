package stone.dal.kernel;

public class KernelRuntimeException extends RuntimeException {

  public KernelRuntimeException() {
  }

  public KernelRuntimeException(String message) {
    super(message);
  }

  public KernelRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public KernelRuntimeException(Throwable cause) {
    super(cause);
  }

  public KernelRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
