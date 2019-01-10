package stone.dal.metadata.meta;


/**
 * Application exception, including error object
 *
 * @author feng.xie
 * @version $Revision:
 */
public class PlatformRuntimeException extends RuntimeException {

	private ErrorObj errorObj;

	public PlatformRuntimeException() {
		super();
	}

	public PlatformRuntimeException(String message) {
		super(message);
	}

	public PlatformRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlatformRuntimeException(Throwable cause) {
		super(cause);
	}

	public ErrorObj getErrorObj() {
		return errorObj;
	}

	public void setErrorObj(ErrorObj errorObj) {
		this.errorObj = errorObj;
	}

	public void merge(PlatformRuntimeException ex) {
		ErrorObj err = ex.getErrorObj();
		errorObj.setMsg(errorObj.getMsg() + "|" + err.getMsg());
	}
}
