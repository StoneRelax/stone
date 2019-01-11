package stone.dal.jdbc.autoconfigure;

import org.springframework.context.annotation.Configuration;

/**
 * @author fengxie
 */
@Configuration
public class DalJdbcAutoConfigure {

//	@Autowired
//	private EntityMetaManager entityMetaManager;
//	@Autowired(required = false)
//	private DalRdbmsResultHandlerSpiImpl resultSetHandler;
//	@Autowired(required = false)
//	private DalSequenceSpi dalSequenceSpi;
//
//	private JdbcRepository dalCrudTemplate;
//	private JdbcTemplate jdbcTemplate;
//	private LazyLoadQueryMetaBuilder deferredDataLoader;
//
//	@Bean(name = "dummyDataSource")
//	@ConfigurationProperties(prefix = "datasource.dummy")
//	public DataSource dummyDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@PostConstruct
//	public void init() {
//		DalRdbmsQueryRunner.Factory queryFactory = DalRdbmsQueryRunner.factory();
//		deferredDataLoader = new LazyLoadQueryMetaBuilder(entityMetaManager);
//		if (resultSetHandler == null) {
//			queryFactory.rdbmsResultSetHandler(getDefaultResultSetHandler(queryFactory.getRunner()));
//		} else {
//			queryFactory.rdbmsResultSetHandler(resultSetHandler);
//		}
//		queryFactory.entityMetaManager(entityMetaManager);
//		DalRdbmsDmlRunner.Factory dmlFactory = DalRdbmsDmlRunner.factory();
//		dmlFactory.entityMetaManager(entityMetaManager);
//		dmlFactory.dalSequence(dalSequenceSpi);
//
//		jdbcTemplate = new JdbcTemplateImpl(queryFactory.build(), dmlFactory.build(),
//				JdbcDclRunner.factory().build());
//		dalCrudTemplate = new JdbcRepositoryImpl(jdbcTemplate, entityMetaManager, deferredDataLoader);
//	}
//
//	@Bean
//	public JdbcTemplate getDalRdbmsRunner() {
//		return jdbcTemplate;
//	}
//
//	@Bean
//	public JdbcRepository getDalCrudTemplate() {
//		return dalCrudTemplate;
//	}
//
//	//todo by xzang, not found this class
////	@Bean
////	public DalRdbmsObjFactory getRdbmsObjFactory() {
////		return new DalRdbmsObjFactory(entityMetaManager);
////	}
//
//
//	DalRdbmsResultHandlerSpi getDefaultResultSetHandler(DalRdbmsQueryRunner queryRunner) {
//		return new DalRdbmsResultHandlerSpiImpl(deferredDataLoader);
//	}
}
