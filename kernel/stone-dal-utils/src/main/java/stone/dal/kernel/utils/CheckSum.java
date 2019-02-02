package stone.dal.kernel.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengxie
 */
public class CheckSum {
  private static Logger logger = LoggerFactory.getLogger(CheckSum.class);

  public static String checkSum(InputStream is) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      byte[] dataBytes = new byte[1024];
      int nread = 0;
      while ((nread = is.read(dataBytes)) != -1) {
        md.update(dataBytes, 0, nread);
      }
      byte[] mdbytes = md.digest();

      StringBuilder sb = new StringBuilder("");
      for (byte mdbyte : mdbytes) {
        sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
      }
      return sb.toString();
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw new KernelRuntimeException(ex);
    }
  }
}
