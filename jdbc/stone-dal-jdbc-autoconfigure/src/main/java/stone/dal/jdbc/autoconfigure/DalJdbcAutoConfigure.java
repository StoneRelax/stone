package stone.dal.jdbc.autoconfigure;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.DBDialectSpi;
import stone.dal.jdbc.JdbcTemplateSpi;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.models.EntityMetaManager;

/**
 * @author fengxie
 */
@Configuration
public class DalJdbcAutoConfigure {

  @Autowired
  private EntityMetaManager entityMetaManager;

  private RdbmsEntityManager rdbmsEntityManager;

  //	@Autowired
//	private EntityMetaManager entityMetaManager;
//	@Autowired(required = false)
//	private JdbcResultHandlerSpiImpl resultSetHandler;
//	@Autowired(required = false)
//	private DalSequenceSpi dalSequenceSpi;
  @Autowired
  private JdbcTemplateSpi jdbcTemplateSpi;

  @Autowired
  private DBDialectSpi dbDialectSpi;

  //
  private StJpaRepository jpaRepository;

  private StJdbcTemplate jdbcTemplate;

  private RelationQueryBuilder relationQueryBuilder;

  @PostConstruct
  public void init() {
    rdbmsEntityManager = new RdbmsEntityManager(entityMetaManager);
    relationQueryBuilder = new RelationQueryBuilder(rdbmsEntityManager);
    jdbcTemplate = new StJdbcTemplateImpl(jdbcTemplateSpi, dbDialectSpi, relationQueryBuilder, rdbmsEntityManager);
    jpaRepository = new StJpaRepositoryImpl(jdbcTemplate, rdbmsEntityManager, relationQueryBuilder);
//		JdbcQuerySpi.Factory queryFactory = JdbcQuerySpi.factory();
//		deferredDataLoader = new RelationQueryBuilder(entityMetaManager);
//		if (resultSetHandler == null) {
//			queryFactory.rdbmsResultSetHandler(getDefaultResultSetHandler(queryFactory.getRunner()));
//		} else {
//			queryFactory.rdbmsResultSetHandler(resultSetHandler);
//		}
//		queryFactory.entityMetaManager(entityMetaManager);
//		JdbcTemplateSpi.Factory dmlFactory = JdbcTemplateSpi.factory();
//		dmlFactory.entityMetaManager(entityMetaManager);
//		dmlFactory.dalSequence(dalSequenceSpi);
//
//		jdbcTemplate = new StJdbcTemplateImpl(queryFactory.build(), dmlFactory.build(),
//				JdbcDclSpi.factory().build());
//		dalCrudTemplate = new StJpaRepositoryImpl(jdbcTemplate, entityMetaManager, deferredDataLoader);
  }

  @Bean
  public StJpaRepository getJpaRepository() {
    return jpaRepository;
  }

  @Bean
  public StJdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

//
//	JdbcResultHandlerSpi getDefaultResultSetHandler(JdbcQuerySpi queryRunner) {
//		return new JdbcResultHandlerSpiImpl(deferredDataLoader);
//	}
}
