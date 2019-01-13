package stone.dal.jdbc.api;

import java.io.InputStream;
import java.util.List;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.Page;

/**
 * @author fengxie
 */
public interface StJdbcTemplate {
	/**
	 * Execute runQuery with a specified <code>com.rab.publication.persistence.SqlQueryMeta</code>
	 *
	 * @param queryMeta runQuery meta
	 * @return result list, if runQuery meta has mapping class, then the result contains instances of mapping class. Otherwise, the result
	 * contains map instances.
	 */
	<T> List<T> runQuery(SqlQueryMeta queryMeta);

	/**
	 * Execute pagination runQuery with specified meta name, and parameters
	 *
	 * @param queryMeta runQuery meta
	 * @return result list, if runQuery meta has mapping class, then the result contains instances of mapping class. Otherwise, the result
	 * contains map instances.
	 */
	<T> Page<T> pagination(SqlQueryMeta queryMeta);

	/**
	 * Execute runDml operation
	 *
	 * @param baseMeta jdbc meta
	 */
	int runDml(SqlDmlDclMeta baseMeta);

	/**
	 * Execute dml
	 *
	 * @param sql Sql
	 */
	int runDcl(String sql);

	/**
	 * Execute sql file
	 *
	 * @param inputStream Inputstream of sqlfile
	 * @return Execute code
	 */
	List<ExecResult> runSqlStream(InputStream inputStream);

	/**
	 * Execute sql script
	 *
	 * @param sqlScripts sql scripts which splitted by ;
	 * @return Sql errors
	 */
	List<ExecResult> runSqlScript(String sqlScripts);

//  <T extends BaseDo> T runFind(SqlCondition condition);
}
