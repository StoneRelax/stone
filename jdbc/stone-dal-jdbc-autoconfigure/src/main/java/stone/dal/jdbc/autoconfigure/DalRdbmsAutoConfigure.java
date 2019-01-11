package stone.dal.jdbc.autoconfigure;

import org.springframework.context.annotation.Configuration;

/**
 * @author fengxie
 */
@Configuration
public class DalRdbmsAutoConfigure {

//	@Autowired
//	private DalEntityMetaManager dalEntityMetaManager;
//	@Autowired(required = false)
//	private DalRdbmsResultHandlerSpiImpl resultSetHandler;
//	@Autowired(required = false)
//	private DalSequenceSpi dalSequenceSpi;
//
//	private DalRdbmsCrudTemplate dalCrudTemplate;
//	private DalRdbmsRunner dalRdbmsRunner;
//	private DalLazyLoadQueryMetaBuilder deferredDataLoader;
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
//		deferredDataLoader = new DalLazyLoadQueryMetaBuilder(dalEntityMetaManager);
//		if (resultSetHandler == null) {
//			queryFactory.rdbmsResultSetHandler(getDefaultResultSetHandler(queryFactory.getRunner()));
//		} else {
//			queryFactory.rdbmsResultSetHandler(resultSetHandler);
//		}
//		queryFactory.dalEntityMetaManager(dalEntityMetaManager);
//		DalRdbmsDmlRunner.Factory dmlFactory = DalRdbmsDmlRunner.factory();
//		dmlFactory.dalEntityMetaManager(dalEntityMetaManager);
//		dmlFactory.dalSequence(dalSequenceSpi);
//
//		dalRdbmsRunner = new DalRdbmsRunnerImpl(queryFactory.build(), dmlFactory.build(),
//				JdbcDclRunner.factory().build());
//		dalCrudTemplate = new DalRdbmsCrudTemplateImpl(dalRdbmsRunner, dalEntityMetaManager, deferredDataLoader);
//	}
//
//	@Bean
//	public DalRdbmsRunner getDalRdbmsRunner() {
//		return dalRdbmsRunner;
//	}
//
//	@Bean
//	public DalRdbmsCrudTemplate getDalCrudTemplate() {
//		return dalCrudTemplate;
//	}
//
//	//todo by xzang, not found this class
////	@Bean
////	public DalRdbmsObjFactory getRdbmsObjFactory() {
////		return new DalRdbmsObjFactory(dalEntityMetaManager);
////	}
//
//
//	DalRdbmsResultHandlerSpi getDefaultResultSetHandler(DalRdbmsQueryRunner queryRunner) {
//		return new DalRdbmsResultHandlerSpiImpl(deferredDataLoader);
//	}
}
