package stone.dal.rdbms.impl;


import org.springframework.cache.CacheManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.kernel.LogUtils;
import stone.dal.metadata.meta.ErrorObj;
import stone.dal.rdbms.spi.DBDialectSpi;
import stone.dal.rdbms.spi.DalRdbmsResultHandlerSpi;

import javax.sql.DataSource;
import java.sql.*;

import static stone.dal.kernel.KernelUtils.str_emp;

/**
 * @author fengxie
 */
public abstract class DalKernel {
	protected CacheManager cacheManager;
	protected DalEntityMetaManager dalEntityMetaManager;
	protected DalRdbmsResultHandlerSpi rdbmsResultSetHandler;

	public DalKernel() {
	}

	protected DBDialectSpi getDialect(String schema) {
		String type = RdbmsDataSourceManager.getInstance().getType(schema);
		return DBDialectManager.getInstance().getDialect(type);
	}

	protected String getType(String schema) {
		return RdbmsDataSourceManager.getInstance().getType(schema);
	}


	protected Connection getConnection(String schema) {
		try {
			DataSource dataSource = RdbmsDataSourceManager.getInstance().getDataSource(schema);
			return DataSourceUtils.getConnection(dataSource);
		} catch (Exception e) {
			throw new KernelRuntimeException(e);
		}
	}

	protected KernelRuntimeException wrapException(String schema, Exception e) {
		if (e instanceof SQLException) {
			DBDialectSpi dbDialect = getDialect(schema);
			String errorCode = dbDialect.getExceptionCaused((SQLException) e);
			String exceptionCause = LogUtils.printEx(e);
			KernelRuntimeException ex = new KernelRuntimeException(exceptionCause);
			if (!str_emp(errorCode)) {
				ErrorObj errorObj = new ErrorObj(errorCode, exceptionCause);
				ex.setErrorObj(errorObj);
			}
			return ex;
		} else if (e instanceof KernelRuntimeException) {
			return (KernelRuntimeException) e;
		} else {
			return new KernelRuntimeException(e);
		}
	}

	protected void initParams(PreparedStatement ps, Object[] _params) throws SQLException {
		if (_params != null) {
			for (int i = 0; i < _params.length; i++) {
				Object param = _params[i];
				setStatementParams(ps, param, i);
			}
		}
	}

	protected void release(String schema, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw wrapException(schema, e);
			}
		}
	}

	protected void release(String schema, PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException ex) {
				throw wrapException(schema, ex);
			}
		}
	}

	protected void release(String schema, Connection connection) {
		if (connection != null) {
			DataSource dataSource = RdbmsDataSourceManager.getInstance().getDataSource(schema);
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	protected void setStatementParams(PreparedStatement ps, Object obj, int index) throws SQLException {
		int _index = index + 1;
		if (obj instanceof Timestamp) {
			ps.setTimestamp(_index, (Timestamp) obj);
		} else if (obj instanceof java.util.Date) {
			java.sql.Date date = new java.sql.Date(((java.util.Date) obj).getTime());
			ps.setDate(_index, date);
		} else if (obj instanceof Long) {
			ps.setLong(_index, (Long) obj);
		} else if (obj instanceof Integer) {
			ps.setInt(_index, (Integer) obj);
		} else if (obj instanceof String) {
			ps.setString(_index, (String) obj);
		} else {
			ps.setObject(_index, obj);
		}
	}

	protected PreparedStatement createUpdatableStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	public static abstract class Factory<T extends DalKernel> {

		protected T runner;

		public Factory() {
			runner = initRunner();
		}

		protected abstract T initRunner();


		public Factory rdbmsResultSetHandler(DalRdbmsResultHandlerSpi rdbmsResultSetHandler) {
			runner.rdbmsResultSetHandler = rdbmsResultSetHandler;
			return this;
		}

		public Factory cacheManager(CacheManager cacheManager) {
			runner.cacheManager = cacheManager;
			return this;
		}

		public Factory dalEntityMetaManager(DalEntityMetaManager dalEntityMetaManager) {
			runner.dalEntityMetaManager = dalEntityMetaManager;
			return this;
		}

		public T build() {
			return runner;
		}

	}
}
