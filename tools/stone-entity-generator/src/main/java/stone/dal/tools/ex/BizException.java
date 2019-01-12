package stone.dal.tools.ex;

/**
 * todo:description
 *
 * @author feng.xie
 * @version $Revision:
 */
public class BizException extends BaseException {

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }
}
