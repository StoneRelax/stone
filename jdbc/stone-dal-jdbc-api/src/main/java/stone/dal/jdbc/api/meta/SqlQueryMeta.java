package stone.dal.jdbc.api.meta;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import stone.dal.kernel.utils.StringUtils;

/**
 * @author fengxie
 */
public abstract class SqlQueryMeta {

  /**
   * Sql statement
   */
  String sql;

  /**
   * Page info query sql
   */
  String pageQuerySql;

  /**
   * Page total count query sql
   */
  String pageTotalCountQuerySql;

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
  List<ResultSetCallback> callbacks;

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
   * Search 4 modify
   */
  boolean shouldAttached;

  /**
   * Support field mapper
   */
  boolean supportMapper = true;

  /**
   * Support cascade fetching
   */
  boolean one2oneCascadeFetching = false;

  public static SqlQueryMeta bindPageSql(SqlQueryMeta queryMeta, String pageQuerySql, String pageTotalCountQuerySql) {
    queryMeta.pageQuerySql = pageQuerySql;
    queryMeta.pageTotalCountQuerySql = pageTotalCountQuerySql;
    return queryMeta;
  }

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

  public boolean shouldAttached() {
    return shouldAttached;
  }

  public boolean isSupportMapper() {
    return supportMapper;
  }

  public List<ResultSetCallback> getCallbacks() {
    return callbacks;
  }

  public String getSql() {
    return sql;
  }

  public String getPageQuerySql() {
    return pageQuerySql;
  }

  public String getPageTotalCountQuerySql() {
    return pageTotalCountQuerySql;
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

  public interface ResultSetCallback<T> {
    void callback(List<T> rows);
  }

  public static Factory factory() {
    return new Factory();
  }

  public static class Factory {

    private SqlQueryMeta meta = new SqlQueryMeta() {
    };

    private List<ResultSetCallback> callbacks = new ArrayList<>();

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
      meta.shouldAttached = updatable;
      return this;
    }

    public Factory addCallback(ResultSetCallback callback) {
      callbacks.add(callback);
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

    public Factory params(Object... params) {
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
      meta.callbacks = Collections.unmodifiableList(callbacks);
      return meta;
    }

    public Factory join(SqlQueryMeta queryMeta) {
      String sql = StringUtils.replaceNull(meta.getSql());
      sql += queryMeta.sql;
      meta.sql = sql;
      List<Object> parameters = new ArrayList<>();
      parameters.addAll(Arrays.asList(meta.parameters));
      parameters.addAll(Arrays.asList(queryMeta.parameters));
      meta.parameters = parameters.toArray(new Object[0]);
      return this;
    }

  }
}