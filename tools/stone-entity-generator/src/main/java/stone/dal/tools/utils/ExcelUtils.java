package stone.dal.tools.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;

/**
 * Component:  ExcelUtils
 * Description:  ExcelUtils
 * User: feng.xie
 * Date: 29/06/11
 */
public class ExcelUtils {

  public static Workbook getWorkbook(InputStream is) throws Exception {
    return WorkbookFactory.create(is);

  }

  public static boolean cellBool(Cell cell) {
    return cell != null && (cell.getStringCellValue().equalsIgnoreCase("y")
        || booleanValueForString(cell.getStringCellValue()));
  }

  private static boolean booleanValueForString(String value) {
    return "true".equals(value) || "1".equals(value);
  }

  public static boolean booleanValueForBoolean(Boolean value) {
    return value != null && value;
  }

  public static String cellStr(Cell cell) {
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
