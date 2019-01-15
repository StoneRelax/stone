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
   * Execute query with a specified <code>com.rab.publication.persistence.SqlQueryMeta</code>
   *
   * @param queryMeta query meta
   * @return result list, if query meta has mapping class, then the result contains instances of mapping class. Otherwise, the result
	 * contains map instances.
	 */
  <T> List<T> query(SqlQueryMeta queryMeta);

	<T> List<T> queryByCondition(SqlCondition condition);

	<T> T queryOneByCondition(SqlCondition condition);

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
	 * @param inputStream Inputstream of sqlfile
	 * @return Execute code
	 */
	List<ExecResult> execSqlStream(InputStream inputStream);

	/**
	 * Execute sql script
	 *
	 * @param sqlScripts sql scripts which splitted by ;
	 * @return Sql errors
	 */
	List<ExecResult> execSqlScript(String sqlScripts);

//  <T extends BaseDo> T runFind(SqlCondition condition);
}
