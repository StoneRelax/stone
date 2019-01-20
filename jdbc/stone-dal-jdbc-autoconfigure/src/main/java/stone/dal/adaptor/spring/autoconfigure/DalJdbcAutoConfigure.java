package stone.dal.adaptor.spring.autoconfigure;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntityManager;
import stone.dal.adaptor.spring.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.adaptor.spring.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.adaptor.spring.jdbc.impl.dialect.MysqlDialect;
import stone.dal.adaptor.spring.jdbc.impl.dialect.OracleDialect;
import stone.dal.adaptor.spring.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.adaptor.spring.jdbc.spi.DBDialectSpi;
import stone.dal.adaptor.spring.jdbc.spi.JdbcTemplateSpi;
import stone.dal.common.spi.SequenceSpi;
import stone.dal.models.EntityMetaManager;

/**
 * @author fengxie
 */
@Configuration
public class DalJdbcAutoConfigure {

  @Autowired
  private EntityMetaManager entityMetaManager;


  @Autowired
  private JdbcTemplateSpi jdbcTemplateSpi;

  @Autowired
  private SequenceSpi sequenceSpi;

  @Value("${stone.dal.dialect}")
  private String dialectType;

  @Autowired(required = false)
  private DBDialectSpi dialectSpi;

  private StJpaRepository jpaRepository;

  private StJdbcTemplate jdbcTemplate;

  RdbmsEntityManager rdbmsEntityManager;

  @PostConstruct
  public void init() {
    if (dialectSpi == null) {
      dialectSpi = initDialect();
    }
    rdbmsEntityManager = new RdbmsEntityManager(entityMetaManager);
    RelationQueryBuilder relationQueryBuilder = new RelationQueryBuilder(rdbmsEntityManager);
    jdbcTemplate = new StJdbcTemplateImpl(jdbcTemplateSpi, dialectSpi, relationQueryBuilder, rdbmsEntityManager);
    jpaRepository = new StJpaRepositoryImpl(jdbcTemplate, rdbmsEntityManager, relationQueryBuilder, sequenceSpi);
  }

//  @Bean
//  public DBDialectSpi getDialectSpi(){
//    return dialectSpi;
//  }

  @Bean
  public StJpaRepository getJpaRepository() {
    return jpaRepository;
  }

  @Bean
  public StJdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Bean
  public RdbmsEntityManager getRdbmsEntityManager(){
    return  rdbmsEntityManager;
  }

  private DBDialectSpi initDialect() {
    if ("mysql".equalsIgnoreCase(dialectType)) {
      //todo:configure mysql errors
      return new MysqlDialect(new HashMap<>());
    } else if ("oracle".equalsIgnoreCase(dialectType)) {
      return new OracleDialect(new HashMap<>());
    }
    return null;
  }

}
