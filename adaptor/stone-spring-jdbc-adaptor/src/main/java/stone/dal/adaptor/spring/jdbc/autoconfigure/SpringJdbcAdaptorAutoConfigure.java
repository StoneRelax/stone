package stone.dal.adaptor.spring.jdbc.autoconfigure;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.adaptor.spring.autoconfigure.DalAutoConfigure;
import stone.dal.adaptor.spring.jdbc.impl.JdbcTemplateSpiImpl;
import stone.dal.common.models.EntityMetaManager;
import stone.dal.common.spi.ClobResolverSpi;
import stone.dal.common.spi.ResultSetClobHandler;
import stone.dal.common.spi.SequenceSpi;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.impl.DefaultClobResolverImpl;
import stone.dal.jdbc.impl.DefaultResultSetClobHandler;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.jdbc.impl.dialect.MysqlDialect;
import stone.dal.jdbc.impl.dialect.OracleDialect;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.jdbc.spi.DBDialectSpi;

@Configuration
@Import(DalAutoConfigure.class)
public class SpringJdbcAdaptorAutoConfigure {

  private StJpaRepositoryImpl stJpaRepository;

  private StJdbcTemplate stJdbcTemplate;

  RdbmsEntityManager rdbmsEntityManager;

  @Autowired(required = false)
  private ResultSetClobHandler resultSetClobHandler;

  public SpringJdbcAdaptorAutoConfigure(
      @Autowired(required = false) DBDialectSpi dialectSpi,
      @Autowired EntityMetaManager entityMetaManager,
      @Autowired SequenceSpi sequenceSpi,
      @Value("${stone.dal.dialect}") String dialectType,
      @Autowired(required = false) ClobResolverSpi clobResolverSpi,
      @Autowired JdbcTemplate jdbcTemplate) {
    if (dialectSpi == null) {
      dialectSpi = initDialect(dialectType);
    }
    rdbmsEntityManager = new RdbmsEntityManager(entityMetaManager);
    RelationQueryBuilder relationQueryBuilder = new RelationQueryBuilder(rdbmsEntityManager);
    if (clobResolverSpi == null) {
      clobResolverSpi = new DefaultClobResolverImpl("clobs");
    }
    if (resultSetClobHandler == null) {
      resultSetClobHandler = new DefaultResultSetClobHandler(clobResolverSpi);
    }
    stJdbcTemplate = new StJdbcTemplateImpl(new JdbcTemplateSpiImpl(jdbcTemplate),
        dialectSpi, relationQueryBuilder, rdbmsEntityManager, resultSetClobHandler);
    stJpaRepository = new StJpaRepositoryImpl(stJdbcTemplate, rdbmsEntityManager, relationQueryBuilder, sequenceSpi,
        clobResolverSpi);
  }

  @Bean("jpaRepository")
  public StJpaRepositoryImpl getStJpaRepository() {
    return stJpaRepository;
  }

  @Bean
  public StJdbcTemplate getStJdbcTemplate() {
    return stJdbcTemplate;
  }

  @Bean
  public RdbmsEntityManager getRdbmsEntityManager() {
    return rdbmsEntityManager;
  }

  private DBDialectSpi initDialect(String dialectType) {
    if ("mysql".equalsIgnoreCase(dialectType)) {
      //todo:configure mysql errors
      return new MysqlDialect(new HashMap<>());
    } else if ("oracle".equalsIgnoreCase(dialectType)) {
      return new OracleDialect(new HashMap<>());
    }
    return null;
  }

}
