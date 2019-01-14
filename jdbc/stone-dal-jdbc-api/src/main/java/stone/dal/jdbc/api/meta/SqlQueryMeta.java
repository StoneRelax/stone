package stone.dal.jdbc.api.meta;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
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
  Object[] parameters = new Object[0];

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

  public interface RowMapper {
    Object mapRow(SqlQueryMeta queryMeta,
        ResultSetMetaData rsmd, int index, ResultSet rs);
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