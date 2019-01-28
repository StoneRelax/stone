package stone.dal.tools.rdbms.init.delta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import stone.dal.kernel.utils.KernelRuntimeException;

import static stone.dal.kernel.utils.KernelUtils.str2Arr;

/**
 * @author fengxie
 */
@Component
public class RdbmsSyncDeltaTestInitializer {

  @Autowired
  @Qualifier("adminJdbcTemplate")
  private JdbcTemplate adminJdbcTemplate;

  @PostConstruct
  public void dbInit() {
    InputStream is = RdbmsSyncDeltaTestInitializer.class.getResourceAsStream("/dbscript.sql");
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        if (!line.startsWith("--")) {
          sb.append(line);
        }
      }
      execSqlScript(sb.toString());
    } catch (IOException e) {
      throw new KernelRuntimeException(e);
    }
  }

  private void execSqlScript(String sqlScripts) {
    String[] sqls = str2Arr(sqlScripts, ";");
    for (String sql : sqls) {
      adminJdbcTemplate.execute(sql);
    }
  }

}