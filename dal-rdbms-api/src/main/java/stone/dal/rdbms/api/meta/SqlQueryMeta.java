package stone.dal.rdbms.api.meta;

import stone.dal.common.api.DalQueryPostHandler;
import stone.dal.rdbms.api.DalRdbmsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fengxie
 */
public abstract class SqlQueryMeta {

	/**
	 * Sql statement
	 */
	String sql;
	/**
	 * parameters
	 */
	Object[] parameters = new Object[0];
	/**
	 * Bean class whose instance might be imported with result value
	 */
	Class mappingClazz = Map.class;
	/**
	 * DB Schema
	 */
	String schema = DalRdbmsConstants.PRIMARY_SCHEMA;

	/**
	 * Callback list
	 */
	DalQueryPostHandler[] postHandlers;
	/**
	 * Page no
	 */
	int pageNo;
	/**
	 * Page size
	 */
	int pageSize = DalRdbmsConstants.DEFAULT_PAGE_SIZE;
	/**
	 * Boolean flag of aop
	 */
	boolean supportFetchMore;
	/**
	 * Max record size
	 */
	int maxSize;
	/**
	 * Cache id used by ehcache
	 */
	String cacheId;
	/**
	 * Search 4 modify
	 */
	boolean updatable;
	/**
	 * Support field mapper
	 */
	boolean supportMapper = true;
	/**
	 * Support cascade fetching
	 */
	boolean one2oneCascadeFetching = false;

	public boolean isOne2oneCascadeFetching() {
		return one2oneCascadeFetching;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isSupportFetchMore() {
		return supportFetchMore;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public String getCacheId() {
		return cacheId;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public boolean isSupportMapper() {
		return supportMapper;
	}

	public DalQueryPostHandler[] getPostHandlers() {
		return postHandlers;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public Class getMappingClazz() {
		return mappingClazz;
	}

	public String getSchema() {
		return schema;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {

		private SqlQueryMeta meta = new SqlQueryMeta() {
		};

		public Factory sql(String sql) {
			meta.sql = sql;
			return this;
		}

		public Factory pageSize(int pageSize) {
			meta.pageSize = pageSize;
			return this;
		}

		public Factory pageNo(int pageNo) {
			meta.pageNo = pageNo;
			return this;
		}

		public Factory updatable(boolean updatable) {
			meta.updatable = updatable;
			return this;
		}

		public Factory schema(String schema) {
			if (schema != null) {
				meta.schema = schema;
			}
			return this;
		}

		public Factory postHandlers(DalQueryPostHandler[] handlers) {
			meta.postHandlers = handlers;
			return this;
		}

		public Factory supportFetchMore(boolean supportFetchMore) {
			meta.supportFetchMore = supportFetchMore;
			return this;
		}

		public Factory maxSize(int maxSize) {
			meta.maxSize = maxSize;
			return this;
		}


		public Factory mappingClazz(Class clazz) {
			meta.mappingClazz = clazz;
			return this;
		}

		public Factory params(Object[] params) {
			meta.parameters = params;
			return this;
		}

		public Factory supportMapper(boolean supportMapper) {
			meta.supportMapper = supportMapper;
			return this;
		}

		public Factory one2oneCascadeFetching(boolean one2oneCascadeFetching) {
			meta.one2oneCascadeFetching = one2oneCascadeFetching;
			return this;
		}

		public SqlQueryMeta build() {
			return meta;
		}

		public Factory join(SqlQueryMeta queryMeta) {
			String sql = meta.getSql();
			sql += queryMeta.sql;
			meta.sql = sql;
			List<Object> parameters = new ArrayList<>();
			parameters.addAll(Arrays.asList(meta.parameters));
			parameters.addAll(Arrays.asList(queryMeta.parameters));
			meta.parameters = parameters.toArray(new Object[parameters.size()]);
			return this;
		}
	}
}