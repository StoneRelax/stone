package stone.dal.jdbc.spring.adaptor.AutoConfigure;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.JdbcQueryRunner;
import stone.dal.jdbc.spring.adaptor.impl.JdbcDclRunnerImpl;
import stone.dal.jdbc.spring.adaptor.impl.JdbcDmlRunnerImpl;
import stone.dal.jdbc.spring.adaptor.impl.JdbcQueryRunnerImpl;

@Configuration
public class SpringJdbcAdaptorAutoConfigure {

  @Value("${spring.datasource.db.url}")
  private String DB_URL;

  @Value("${spring.datasource.db.username}")
  private String DB_USERNAME;

  @Value("${spring.datasource.db.password}")
  private String DB_PASSWORD;

  @Value("${spring.datasource.db.driver-class-name}")
  private String DB_DRIVE_CLASS;

  @Value("${spring.datasource.db.min-idle-size}")
  private int DB_MIN_IDLE;

  @Value("${spring.datasource.db.init-size}")
  private int DB_INIT_SIZE;

  @Value("${spring.datasource.db.max-active-size")
  private int DB_MAX_ACTIVE;

  @Value("${spring.datasource.db.max-idle-size}")
  private int DB_MAX_IDLE;

  @Bean
  @Primary
  public DataSource getDataSource() throws Exception {

    PoolProperties poolProperties = new PoolProperties();

    poolProperties.setUrl(DB_URL);
    poolProperties.setUsername(DB_USERNAME);
    poolProperties.setPassword(DB_PASSWORD);
    poolProperties.setDriverClassName(DB_DRIVE_CLASS);

    poolProperties.setMinIdle(DB_MIN_IDLE);
    poolProperties.setInitialSize(DB_INIT_SIZE);
    poolProperties.setMaxActive(DB_MAX_ACTIVE);
    poolProperties.setMaxIdle(DB_MAX_IDLE);
    poolProperties.setJmxEnabled(true);

    poolProperties.setTestOnBorrow(true);
    poolProperties.setTestOnConnect(true);
    poolProperties.setTestWhileIdle(true);
    poolProperties.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
    DataSource dataSource = new DataSource(poolProperties);
    org.apache.tomcat.jdbc.pool.jmx.ConnectionPool jmxPool = dataSource.createPool().getJmxPool();
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    mBeanServer.registerMBean(jmxPool,
            new ObjectName("com.tomcat.jdbc:type=" + dataSource.getClass().getName() + ",name=jdbc-connection-pool"));
    return dataSource;
  }

  @Bean
  @Primary
  public NamedParameterJdbcTemplate getJDBCTemplate(@Autowired DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  public JdbcDclRunner getDclRunner(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
    return new JdbcDclRunnerImpl(jdbcTemplate);
  }

  @Bean
  public JdbcDmlRunner getDmlRunner(@Autowired NamedParameterJdbcTemplate jdbcTemplate){
    return new JdbcDmlRunnerImpl(jdbcTemplate);
  }
  @Bean
  public JdbcQueryRunner getQueryRunner(@Autowired NamedParameterJdbcTemplate jdbcTemplate){
    return new JdbcQueryRunnerImpl(jdbcTemplate);
  }
}
