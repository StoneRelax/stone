package stone.dal.tools;

import org.apache.commons.lang.StringUtils;

public class ToolsMain {

  public static void main(String[] args) throws Exception {
    DoGenerator doGenerator = new DoGenerator();
    String xlsxPath = System.getProperty("xlsx");
    String targetPath = System.getProperty("target");
    String rootPackage = System.getProperty("rootPackage");
    String basePath = System.getProperty("basePath");
    doGenerator.build(xlsxPath, targetPath, rootPackage, basePath);
  }
}
