package stone.dal.kernel;

import stone.dal.metadata.meta.ErrorObj;

public class KernelRuntimeException extends RuntimeException {
  private ErrorObj errorObj;


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

  public void setErrorObj(ErrorObj errorObj) {
    this.errorObj = errorObj;
  }
}
