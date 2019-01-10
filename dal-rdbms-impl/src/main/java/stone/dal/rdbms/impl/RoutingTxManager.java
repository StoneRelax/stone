package stone.dal.rdbms.impl;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import stone.dal.rdbms.api.DalRdbmsConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author fengxie
 */
public class RoutingTxManager implements PlatformTransactionManager {

	private Map<String, PlatformTransactionManager> txRegistry = new HashMap<>();

	public RoutingTxManager() {
		Set<String> schemas = RdbmsDataSourceManager.getInstance().getSchemas();
		for (String schema : schemas) {
			txRegistry.put(schema, new DataSourceTransactionManager(
					RdbmsDataSourceManager.getInstance().getDataSource(schema)));
		}
	}

	protected PlatformTransactionManager getCurrTxManager() {
		//todo:consider a thread local handle multi context
		return txRegistry.get(DalRdbmsConstants.PRIMARY_SCHEMA);
	}

	@Override
	public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
		return getCurrTxManager().getTransaction(definition);
	}

	@Override
	public void commit(TransactionStatus status) throws TransactionException {
		getCurrTxManager().commit(status);
	}

	@Override
	public void rollback(TransactionStatus status) throws TransactionException {
		getCurrTxManager().rollback(status);
	}
}
