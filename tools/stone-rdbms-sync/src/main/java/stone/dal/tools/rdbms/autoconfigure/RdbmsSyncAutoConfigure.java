package stone.dal.tools.rdbms.autoconfigure;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.adaptor.spring.jdbc.autoconfigure.SpringJdbcAdaptorAutoConfigure;
import stone.dal.tools.rdbms.impl.DBSync;

@Configuration
@Import(SpringJdbcAdaptorAutoConfigure.class)
public class RdbmsSyncAutoConfigure {

  @Autowired
  private Environment env;

  @Bean
  public DBSync getDBSync() {
    return new DBSync();
  }

  private DataSource getAdminDataSource() {
    PoolProperties poolProperties = new PoolProperties();
    poolProperties.setUrl(env.getProperty("spring.datasource.admin.url"));
    poolProperties.setUsername(env.getProperty("spring.datasource.admin.username"));
    poolProperties.setPassword(env.getProperty("spring.datasource.admin.password"));
    poolProperties.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));

    poolProperties.setMinIdle(Integer.parseInt(env.getProperty("spring.datasource.min-idle-size")));
    poolProperties.setInitialSize(Integer.parseInt(env.getProperty("spring.datasource.init-size")));
    poolProperties.setMaxActive(Integer.parseInt(env.getProperty("spring.datasource.max-active-size")));
    poolProperties.setMaxIdle(Integer.parseInt(env.getProperty("spring.datasource.max-idle-size")));

    poolProperties.setTestOnBorrow(true);
    poolProperties.setTestOnConnect(true);
    poolProperties.setTestWhileIdle(true);
    poolProperties.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
    return new DataSource(poolProperties);
  }

  @Bean(name = "adminJdbcTemplate")
  @Qualifier("adminJdbcTemplate")
  public JdbcTemplate getAdminJdbcTemplate() {
    return new JdbcTemplate(getAdminDataSource());
  }
}
