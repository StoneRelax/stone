package stone.dal.tools.ex;

/**
 * todo:description
 *
 * @author feng.xie
 * @version $Revision:
 */
public class AppException extends BaseException {

    private ErrorObj errorObj;

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

    public ErrorObj getErrorObj() {
        return errorObj;
    }

    public void setErrorObj(ErrorObj errorObj) {
        this.errorObj = errorObj;

    }
}
