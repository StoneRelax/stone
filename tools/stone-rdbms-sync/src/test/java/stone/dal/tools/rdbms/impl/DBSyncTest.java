package stone.dal.tools.rdbms.impl;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.tools.rdbms.app.RdbmsSyncTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RdbmsSyncTestApplication.class)
public class DBSyncTest {

  @Autowired
  private DBSync dbSync;

  @Test
  public void testSyncDb() {
    dbSync.syncDb();
  }

  @Test
  public void testGetDbScript() {
    List<String> lines = dbSync.getDbScript(false);
    System.out.println(StringUtils.combineString(lines, "\n"));
  }
}
