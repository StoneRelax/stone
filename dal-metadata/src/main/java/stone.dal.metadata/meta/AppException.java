package stone.dal.metadata.meta;

/**
 * Business exception
 *
 * @author feng.xie
 * @version $Revision:
 */
public class AppException extends BaseException {

	public AppException() {
		super();
	}

	public AppException(String message) {
		super(message);
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(Throwable cause) {
		super(cause);
	}
}
