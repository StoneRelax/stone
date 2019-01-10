package stone.dal.metadata.meta;

/**
 * Base exception
 *
 * @author feng.xie
 * @version $Revision:
 */
public class BaseException extends Exception {

	public BaseException() {
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}
}
