package stone.dal.tools.rdbms.impl;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.tools.rdbms.app.RdbmsSyncDeltaTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RdbmsSyncDeltaTestApplication.class)
public class DBSyncDeltaTest {

  @Autowired
  private DBSync dbSync;

  @Test
  public void testSyncDb() {
    dbSync.syncDb(true, null);
  }

  @Test
  public void testGetDbScript() {
    dbSync.syncDb(false, null);
    List<String> lines = dbSync.getDbScript(false);
    System.out.println(StringUtils.combineString(lines, "\n"));

    List<String> lines2 = dbSync.getDbScript(true);
    System.out.println(StringUtils.combineString(lines2, "\n"));
    Assert.assertTrue(CollectionUtils.isEmpty(lines2));
  }
}
