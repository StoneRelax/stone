package stone.dal.tools.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import stone.dal.kernel.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static stone.dal.kernel.utils.StringUtils.replaceNull;

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

  public static String combineString(List<String> strsInput, String strToken) {
    if (strsInput == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int l = strsInput.size();
    for (int i = 0; i < l; i++) {
      sb.append(replaceNull(strsInput.get(i)));
      if (i < l - 1) {
        sb.append(strToken);
      }
    }
    return sb.toString();
  }

  public static String convertFirstAlphetUpperCase(String str) {
    if (StringUtils.isEmpty(str)) {
      return null;
    } else {
      String firstLetter = str.substring(0, 1).toUpperCase();
      return firstLetter + str.substring(1);
    }
  }

  public static void writeFile(String filePath, byte[] content) {
    writeFile(filePath, content, false);
  }

  private static void writeFile(String filePath, byte[] content, boolean append) {
    if (content != null) {
      File infoFile = new File(filePath);
      if (!infoFile.exists()) {
        File dir = infoFile.getParentFile();
        if (dir != null && !dir.exists()) {
          dir.mkdirs();
        }
      }
      try {
        FileOutputStream fos = new FileOutputStream(infoFile, append);
        fos.write(content);
        fos.flush();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
