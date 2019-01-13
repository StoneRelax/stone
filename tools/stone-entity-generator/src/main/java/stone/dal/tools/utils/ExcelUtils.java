package stone.dal.tools.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Component:  ExcelUtils
 * Description:  ExcelUtils
 * User: feng.xie
 * Date: 29/06/11
 */
public class ExcelUtils {

  public static HSSFWorkbook getWorkbook(InputStream is) throws IOException {
    POIFSFileSystem fs = new POIFSFileSystem(is);
    return new HSSFWorkbook(fs);
  }

  public static boolean cellBool(HSSFCell cell) {
    return cell != null && (cell.getStringCellValue().equalsIgnoreCase("y")
        || booleanValueForString(cell.getStringCellValue()));
  }

  private static boolean booleanValueForString(String value) {
    return "true".equals(value) || "1".equals(value);
  }

  public static boolean booleanValueForBoolean(Boolean value) {
    return value != null && value;
  }

  public static String cellStr(HSSFCell cell) {
    if (cell != null) {
      try {
        return cell.getStringCellValue();
      } catch (Exception ex) {
        double _v = cell.getNumericCellValue();
        String sV = String.valueOf(_v);
        if (sV.contains("E")) {
          return String.valueOf((long) _v);
        }
        return sV;
      }
    }
    return null;
  }

}
