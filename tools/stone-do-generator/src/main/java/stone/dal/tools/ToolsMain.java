package stone.dal.tools;

public class ToolsMain {

  public static void main(String[] args) throws Exception {
    DoGenerator doGenerator = new DoGenerator();
    String xlsxPath = System.getProperty("xlsx");
    String targetPath = System.getProperty("target");
    String rootPackage = System.getProperty("rootPackage");
    doGenerator.build(xlsxPath, targetPath, rootPackage);
  }
}
