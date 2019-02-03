package stone.dal.tools;

public class ToolsMain {

  public static void main(String[] args) throws Exception {
    DoGenerator doGenerator = new DoGenerator();
    String xlsxPath = System.getProperty("xlsx");
    String targetPath = System.getProperty("target");
    String rootPackage = System.getProperty("rootPackage");
    String basePath = System.getProperty("baseApiPath");
    String extensionRulePath = System.getProperty("extensionRulePath");
    doGenerator.build(xlsxPath, targetPath, rootPackage, basePath, extensionRulePath);
  }
}
