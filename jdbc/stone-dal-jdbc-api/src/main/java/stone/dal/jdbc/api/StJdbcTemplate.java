package stone.dal.jdbc.api;

import java.io.InputStream;
import java.util.List;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.Page;

/**
 * @author fengxie
 */
public interface StJdbcTemplate {
  /**
   * Execute query with a specified <code>stone.dal.jdbc.api.meta.SqlQueryMeta</code>
   *
   * @param queryMeta query meta
   * @return result list, if query meta has mapping class, then the result contains instances of mapping class. Otherwise, the result
   * contains map instances.
   */
  <T> List<T> query(SqlQueryMeta queryMeta);

  /**
   * Execute query with a specified <code>stone.dal.jdbc.api.meta.SqlQueryMeta</code>. It returns one object.
   *
   * @param queryMeta query meta
   * @return Single object
   */
  <T> T queryOne(SqlQueryMeta queryMeta);

  /**
   * Execute query with a specified <code>stone.dal.jdbc.api.meta.SqlCondition</code>.
   *
   * @param condition query condition
   * @return List of result
   */
  <T> List<T> query(SqlCondition condition);

  /**
   * Execute query with a specified <code>stone.dal.jdbc.api.meta.SqlCondition</code>. It returns one object.
   *
   * @param condition query condition
   * @return Single object
   */
  <T> T queryOne(SqlCondition condition);

  /**
   * Execute pagination query with specified meta name, and parameters
   *
   * @param queryMeta query meta
   * @return result list, if query meta has mapping class, then the result contains instances of mapping class. Otherwise, the result
   * contains map instances.
   */
  <T> Page<T> pagination(SqlQueryMeta queryMeta);

  /**
   * Execute execDml operation
   *
   * @param baseMeta jdbc meta
   * @return Update rows
   */
  int execDml(SqlDmlDclMeta baseMeta);

  /**
   * Execute dml
   *
   * @param sql Sql
   */
  int execDcl(String sql);

  /**
   * Execute sql file
   *
   * @param inputStream InputStream of sqlfile
   * @return Execute result
   */
  List<ExecResult> execSqlStream(InputStream inputStream);

  /**
   * Execute sql script
   *
   * @param sqlScripts sql scripts which splitted by ;
   * @return Execute result
   */
  List<ExecResult> execSqlScript(String sqlScripts);

}
