package stone.dal.jdbc.api;

import java.io.InputStream;
import java.util.List;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

/**
 * @author fengxie
 */
public interface DalRdbmsRunner {
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
	int runDcl(String schema, String sql);

	/**
	 * Execute sql file
	 *
	 * @param schema      Data source schema
	 * @param inputStream Inputstream of sqlfile
	 * @return Execute code
	 */
	List<ExecResult> runSqlStream(InputStream inputStream, String schema);

	/**
	 * Execute sql file
	 *
	 * @param inputStream Inputstream of sql file
	 */
	List<ExecResult> runSqlStream(InputStream inputStream);

	/**
	 * Fetch fields
	 *
	 * @param queryMeta Query meta
	 * @return Fields information
	 */
//	List<FieldInfo> fetchFields(SqlQueryMeta queryMeta) ;

	/**
	 * Fetch fields
	 *
	 * @param queryMeta Query meta
	 * @return Fields information
	 */
//	List<FieldInfo> fetchFields(SqlQueryMeta queryMeta, DBConnectionMeta connectionMeta) ;

	/**
	 * Execute sql script
	 *
	 * @param sqlScripts sql scripts which splitted by ;
	 * @return Sql errors
	 */
	List<ExecResult> runSqlScript(String sqlScripts, String dsSchema);

	/**
	 * Run insert operation
	 *
	 * @param obj Dal obj
	 */
	void runInsert(BaseDo obj);

	/**
	 * Run delete operation
	 *
	 * @param obj Dal obj
	 */
	void runDelete(BaseDo obj);

	/**
	 * Run get operation
	 *
	 * @param pk Pk object
	 * @return BaseDoect
	 */
	<T extends BaseDo> T runFindOne(BaseDo pk);
}
