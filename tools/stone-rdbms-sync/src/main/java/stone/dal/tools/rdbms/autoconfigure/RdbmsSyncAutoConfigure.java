package stone.dal.tools.rdbms.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.tools.rdbms.impl.DBSync;

@Configuration
public class RdbmsSyncAutoConfigure {

  @Bean
  public DBSync getDBSync() {
    return new DBSync();
  }
}
