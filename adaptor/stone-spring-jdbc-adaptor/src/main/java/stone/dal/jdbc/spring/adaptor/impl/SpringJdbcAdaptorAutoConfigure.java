package stone.dal.jdbc.spring.adaptor.impl;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.JdbcQueryRunner;

@Configuration
public class SpringJdbcAdaptorAutoConfigure {

//  @Value("${spring.datasource.dbUrl.url}")
//  private String dbUrl;
//
//  @Value("${spring.datasource.dbUrl.username}")
//  private String DB_USERNAME;
//
//  @Value("${spring.datasource.dbUrl.password}")
//  private String DB_PASSWORD;
//
//  @Value("${spring.datasource.dbUrl.driver-class-name}")
//  private String DB_DRIVE_CLASS;
//
//  @Value("${spring.datasource.dbUrl.min-idle-size}")
//  private int DB_MIN_IDLE;
//
//  @Value("${spring.datasource.dbUrl.init-size}")
//  private int DB_INIT_SIZE;
//
//  @Value("${spring.datasource.dbUrl.max-active-size")
//  private int DB_MAX_ACTIVE;
//
//  @Value("${spring.datasource.dbUrl.max-idle-size}")
//  private int DB_MAX_IDLE;
//
//  @Bean
//  @Primary
//  public DataSource getDataSource() throws Exception {
//    PoolProperties poolProperties = new PoolProperties();
//    poolProperties.setUrl(dbUrl);
//    poolProperties.setUsername(DB_USERNAME);
//    poolProperties.setPassword(DB_PASSWORD);
//    poolProperties.setDriverClassName(DB_DRIVE_CLASS);
//
//    poolProperties.setMinIdle(DB_MIN_IDLE);
//    poolProperties.setInitialSize(DB_INIT_SIZE);
//    poolProperties.setMaxActive(DB_MAX_ACTIVE);
//    poolProperties.setMaxIdle(DB_MAX_IDLE);
//    poolProperties.setJmxEnabled(true);
//
//    poolProperties.setTestOnBorrow(true);
//    poolProperties.setTestOnConnect(true);
//    poolProperties.setTestWhileIdle(true);
//    poolProperties.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
//    DataSource dataSource = new DataSource(poolProperties);
//    org.apache.tomcat.jdbc.pool.jmx.ConnectionPool jmxPool = dataSource.createPool().getJmxPool();
//    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//    mBeanServer.registerMBean(jmxPool,
//            new ObjectName("com.tomcat.jdbc:type=" + dataSource.getClass().getName() + ",name=jdbc-connection-pool"));
//    return dataSource;
//  }

  @Bean
  @Primary
  public NamedParameterJdbcTemplate getJDBCTemplate(@Autowired DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  public JdbcDclRunner getDclRunner(@Autowired JdbcTemplate jdbcTemplate) {
    return new JdbcDclRunnerImpl(jdbcTemplate);
  }

  @Bean
  public JdbcDmlRunner getDmlRunner(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
    return new JdbcDmlRunnerImpl(jdbcTemplate);
  }

  @Bean
  public JdbcQueryRunner getQueryRunner(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
    return new JdbcQueryRunnerImpl(jdbcTemplate);
  }
}
