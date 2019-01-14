package stone.dal.jdbc.autoconfigure;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.jdbc.impl.dialect.MysqlDialect;
import stone.dal.jdbc.impl.dialect.OracleDialect;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.jdbc.spi.JdbcTemplateSpi;
import stone.dal.jdbc.spi.SequenceSpi;
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
  private DBDialectSpi dbDialectSpi;

  @Autowired
  private SequenceSpi sequenceSpi;

  @Value("${st.db.dialect}")
  private String dialectType;

  @Autowired(required = false)
  private DBDialectSpi dialectSpi;

  private StJpaRepository jpaRepository;

  private StJdbcTemplate jdbcTemplate;

  @PostConstruct
  public void init() {
    if (dialectSpi == null) {
      dialectSpi = getDialect();
    }
    RdbmsEntityManager rdbmsEntityManager = new RdbmsEntityManager(entityMetaManager);
    RelationQueryBuilder relationQueryBuilder = new RelationQueryBuilder(rdbmsEntityManager);
    jdbcTemplate = new StJdbcTemplateImpl(jdbcTemplateSpi, dbDialectSpi, relationQueryBuilder, rdbmsEntityManager);
    jpaRepository = new StJpaRepositoryImpl(jdbcTemplate, rdbmsEntityManager, relationQueryBuilder, sequenceSpi);
  }

  @Bean
  public StJpaRepository getJpaRepository() {
    return jpaRepository;
  }

  @Bean
  public StJdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Bean
  public DBDialectSpi getDialect() {
    if ("mysql".equalsIgnoreCase(dialectType)) {
      //todo:configure mysql errors
      return new MysqlDialect(new HashMap<>());
    } else if ("oracle".equalsIgnoreCase(dialectType)) {
      return new OracleDialect(new HashMap<>());
    }
    return null;
  }

}
