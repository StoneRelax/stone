package stone.dal.rdbms.api;

import stone.dal.common.api.DalObj;
import stone.dal.metadata.meta.Page;
import stone.dal.rdbms.api.meta.ExecResult;
import stone.dal.rdbms.api.meta.SqlCondition;
import stone.dal.rdbms.api.meta.SqlDmlDclMeta;
import stone.dal.rdbms.api.meta.SqlQueryMeta;

import java.io.InputStream;
import java.util.List;

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
	void runInsert(DalObj obj);

	/**
	 * Run delete operation
	 *
	 * @param obj Dal obj
	 */
	void runDelete(DalObj obj);

	/**
	 * Run insert operation with a given schema
	 *
	 * @param obj    Dal obj
	 * @param schema Schema
	 */
	void runInsert(DalObj obj, String schema);

	/**
	 * Run update operation with a given schema
	 *
	 * @param obj    Dal obj
	 * @param schema Schema
	 */
	void runUpdate(DalObj obj, String schema);

	/**
	 * Run get operation
	 *
	 * @param pk Pk object
	 * @return DalObject
	 */
	<T extends DalObj> T runFindOne(DalObj pk);

	/**
	 * Run get operation with a given schema
	 *
	 * @param pk     Dal obj with primary keys
	 * @param schema Schema
	 * @param <T>    Type extends Dal
	 * @return Dal object
	 */
	<T extends DalObj> T runFindOne(DalObj pk, String schema);

	/**
	 * Run find by a given dal condition
	 *
	 * @param condition Dal condition
	 * @param schema    Db schema
	 * @param <T>       Dal object type
	 * @return Dal object
	 */
	<T extends DalObj> T runFindOne(SqlCondition condition, String schema);

	/**
	 * Run find by a given dal condition, returns more than one object
	 *
	 * @param condition Sql Condition
	 * @param schema    Db schema
	 * @return Dal objects
	 */
	<T> List<T> runFindMany(SqlCondition condition, String schema);
}
