package stone.dal.tools.rdbms.init;

import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;

/**
 * @author fengxie
 */
@Component
public class RdbmsSyncTestInitializer {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @PostConstruct
  public void dbInit() {
    InputStream is = RdbmsSyncTestInitializer.class.getResourceAsStream("/dbscript.sql");
    stJdbcTemplate.execSqlStream(is);
  }

}