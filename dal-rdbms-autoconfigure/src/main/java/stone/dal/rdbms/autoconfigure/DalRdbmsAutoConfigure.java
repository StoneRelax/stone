package stone.dal.rdbms.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.rdbms.api.DalRdbmsCrudTemplate;
import stone.dal.rdbms.api.DalRdbmsRunner;
import stone.dal.rdbms.impl.*;
import stone.dal.rdbms.impl.utils.DalLazyLoadQueryMetaBuilder;
import stone.dal.rdbms.spi.DalRdbmsResultHandlerSpi;
import stone.dal.spi.DalSequenceSpi;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @author fengxie
 */
@Configuration
public class DalRdbmsAutoConfigure {

	@Autowired
	private DalEntityMetaManager dalEntityMetaManager;
	@Autowired(required = false)
	private DalRdbmsResultHandlerSpiImpl resultSetHandler;
	@Autowired(required = false)
	private DalSequenceSpi dalSequenceSpi;

	private DalRdbmsCrudTemplate dalCrudTemplate;
	private DalRdbmsRunner dalRdbmsRunner;
	private DalLazyLoadQueryMetaBuilder deferredDataLoader;

	@Bean(name = "dummyDataSource")
	@ConfigurationProperties(prefix = "datasource.dummy")
	public DataSource dummyDataSource() {
		return DataSourceBuilder.create().build();
	}

	@PostConstruct
	public void init() {
		DalRdbmsQueryRunner.Factory queryFactory = DalRdbmsQueryRunner.factory();
		deferredDataLoader = new DalLazyLoadQueryMetaBuilder(dalEntityMetaManager);
		if (resultSetHandler == null) {
			queryFactory.rdbmsResultSetHandler(getDefaultResultSetHandler(queryFactory.getRunner()));
		} else {
			queryFactory.rdbmsResultSetHandler(resultSetHandler);
		}
		queryFactory.dalEntityMetaManager(dalEntityMetaManager);
		DalRdbmsDmlRunner.Factory dmlFactory = DalRdbmsDmlRunner.factory();
		dmlFactory.dalEntityMetaManager(dalEntityMetaManager);
		dmlFactory.dalSequence(dalSequenceSpi);

		dalRdbmsRunner = new DalRdbmsRunnerImpl(queryFactory.build(), dmlFactory.build(),
				DalRdbmsDclRunner.factory().build());
		dalCrudTemplate = new DalRdbmsCrudTemplateImpl(dalRdbmsRunner, dalEntityMetaManager, deferredDataLoader);
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new RoutingTxManager();
	}

	@Bean
	public DalRdbmsRunner getDalRdbmsRunner() {
		return dalRdbmsRunner;
	}

	@Bean
	public DalRdbmsCrudTemplate getDalCrudTemplate() {
		return dalCrudTemplate;
	}

	//todo by xzang, not found this class
//	@Bean
//	public DalRdbmsObjFactory getRdbmsObjFactory() {
//		return new DalRdbmsObjFactory(dalEntityMetaManager);
//	}


	DalRdbmsResultHandlerSpi getDefaultResultSetHandler(DalRdbmsQueryRunner queryRunner) {
		return new DalRdbmsResultHandlerSpiImpl(deferredDataLoader);
	}
}
