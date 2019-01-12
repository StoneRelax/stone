package stone.dal.tools.ex;

import java.io.Serializable;

/**
 * todo:description
 *
 * @author feng.xie
 * @version $Revision:
 */
public class ErrorObj implements Serializable{
    private String code;
    private String msg;

    public ErrorObj(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
