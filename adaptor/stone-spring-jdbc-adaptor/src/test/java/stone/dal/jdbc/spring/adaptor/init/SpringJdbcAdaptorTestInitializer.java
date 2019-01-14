package stone.dal.jdbc.spring.adaptor.init;

import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stone.dal.jdbc.api.StJdbcTemplate;

/**
 * @author fengxie
 */
@Component
public class SpringJdbcAdaptorTestInitializer {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @PostConstruct
  public void dbInit() {
    InputStream is = SpringJdbcAdaptorTestInitializer.class.getResourceAsStream("/dbscript.sql");
    stJdbcTemplate.runSqlStream(is);
  }
}
