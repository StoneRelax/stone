package stone.dal.jdbc.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.api.JdbcTemplate;
import stone.dal.jdbc.api.JpaRepository;
import stone.dal.models.EntityMetaManager;

/**
 * @author fengxie
 */
@Configuration
public class DalJdbcAutoConfigure {

  private EntityMetaManager entityMetaManager;

//	@Autowired
//	private EntityMetaManager entityMetaManager;
//	@Autowired(required = false)
//	private JdbcResultHandlerSpiImpl resultSetHandler;
//	@Autowired(required = false)
//	private DalSequenceSpi dalSequenceSpi;
//
private JpaRepository jpaRepository;

  private JdbcTemplate jdbcTemplate;
//	private LazyLoadQueryMetaBuilder deferredDataLoader;
//
//	@PostConstruct
//	public void init() {
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
//	}

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
