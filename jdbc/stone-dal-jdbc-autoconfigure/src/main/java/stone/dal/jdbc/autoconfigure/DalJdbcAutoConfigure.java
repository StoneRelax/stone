package stone.dal.jdbc.autoconfigure;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.JdbcQueryRunner;
import stone.dal.jdbc.api.JdbcTemplate;
import stone.dal.jdbc.api.JpaRepository;
import stone.dal.jdbc.impl.JdbcTemplateImpl;
import stone.dal.jdbc.impl.JpaRepositoryImpl;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.utils.LazyLoadQueryMetaBuilder;
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
  private JdbcQueryRunner queryRunner;

  @Autowired
  private JdbcDclRunner dclRunner;

  @Autowired
  private JdbcDmlRunner dmlRunner;

  //
  private JpaRepository jpaRepository;

  private JdbcTemplate jdbcTemplate;

  private LazyLoadQueryMetaBuilder deferredDataLoader;

  @PostConstruct
  public void init() {
    rdbmsEntityManager = new RdbmsEntityManager(entityMetaManager);
    jdbcTemplate = new JdbcTemplateImpl(queryRunner, dmlRunner, dclRunner);
    jpaRepository = new JpaRepositoryImpl(jdbcTemplate, rdbmsEntityManager, deferredDataLoader);
//		JdbcQueryRunner.Factory queryFactory = JdbcQueryRunner.factory();
//		deferredDataLoader = new LazyLoadQueryMetaBuilder(entityMetaManager);
//		if (resultSetHandler == null) {
//			queryFactory.rdbmsResultSetHandler(getDefaultResultSetHandler(queryFactory.getRunner()));
//		} else {
//			queryFactory.rdbmsResultSetHandler(resultSetHandler);
//		}
//		queryFactory.entityMetaManager(entityMetaManager);
//		JdbcDmlRunner.Factory dmlFactory = JdbcDmlRunner.factory();
//		dmlFactory.entityMetaManager(entityMetaManager);
//		dmlFactory.dalSequence(dalSequenceSpi);
//
//		jdbcTemplate = new JdbcTemplateImpl(queryFactory.build(), dmlFactory.build(),
//				JdbcDclRunner.factory().build());
//		dalCrudTemplate = new JpaRepositoryImpl(jdbcTemplate, entityMetaManager, deferredDataLoader);
  }

  @Bean
  public JpaRepository getJpaRepository() {
    return jpaRepository;
  }

  @Bean
  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

//
//	JdbcResultHandlerSpi getDefaultResultSetHandler(JdbcQueryRunner queryRunner) {
//		return new JdbcResultHandlerSpiImpl(deferredDataLoader);
//	}
}
