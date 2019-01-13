package stone.dal.jdbc.api.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stone.dal.models.DalQueryPostHandler;

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
	Map<String,Object> parameters = new HashMap<>();
	/**
	 * Bean class whose instance might be imported with result value
	 */
	Class mappingClazz = Map.class;
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
	int pageSize = 50;
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

	public Map<String,Object> getParameters() {
		return parameters;
	}

	public Class getMappingClazz() {
		return mappingClazz;
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

		public Factory params(Map<String,Object> params) {
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
			Map<String,Object>parameters = new HashMap<>();
			parameters.putAll(queryMeta.parameters);
			parameters.putAll(meta.parameters);
			meta.parameters = parameters;
			return this;
		}
	}
}