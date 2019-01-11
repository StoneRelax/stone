package stone.dal.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import stone.dal.jdbc.api.meta.DBField;

/**
 * @author fengxie
 */
public interface DBDialectSpi {

	String COLUMN_NAME = "column_name";
	String REF_COLUMN_NAME = "ref_column_name";
	String COLUMN_TYPE = "column_type";
	String COLUMN_SIZE = "column_size";
	String DECIMAL_DIGITS = "decimal_digits";
	String NUM_PREC_RADIX = "num_prec_radix";
	String NULLABLE = "nullable";
	String SQL_DATA_TYPE = "sql_data_type";
	String TABLE_NAME = "table_name";
	String KEY_SEQUENCE = "key_seq";
	String PK_NAME = "pk_name";
	String INDEX_NAME = "index_name";
	String NON_UNIQUE = "non_unique";
	String INDEX_TYPE = "index_type";
	String ASC_OR_DESC = "asc_or_desc";

	/**
	 * interpreter resultSet and put column information to input map
	 *
	 * @param column    map storing column's information
	 * @param resultSet <code>java.sql.ResultSet</code>
	 * @throws SQLException <code>SQLException</code>
	 */
	void interpreterColumnInfo(Map<String, Object> column, ResultSet resultSet) throws SQLException;

	/**
	 * interpreter resultSet and put primarykey information to input map
	 *
	 * @param mapPK     map storing column's information
	 * @param resultSet <code>java.sql.ResultSet</code>
	 * @throws SQLException <code>SQLException</code>
	 */
	void interpreterPrimaryKeyInfo(Map<String, Object> mapPK, ResultSet resultSet) throws SQLException;

	/**
	 * interpreter resultSet and put primarykey information to input map
	 *
	 * @param mapFK     map storing column's information
	 * @param resultSet <code>java.sql.ResultSet</code>
	 * @throws SQLException <code>SQLException</code>
	 */
	void interpreterImportedKeyInfo(Map<String, Object> mapFK, ResultSet resultSet) throws SQLException;

	/**
	 * interpreter resultSet and put primarykey information to input map
	 *
	 * @param mapFK     map storing column's information
	 * @param resultSet <code>java.sql.ResultSet</code>
	 * @throws SQLException <code>SQLException</code>
	 */
	void interpreterExportKeyInfo(Map<String, Object> mapFK, ResultSet resultSet) throws SQLException;

	/**
	 * interpreter resultSet and put index key information to input map
	 *
	 * @param mapIndex  map storing column's information
	 * @param resultSet <code>java.sql.ResultSet</code>
	 * @throws SQLException <code>SQLException</code>
	 */
	void interpreterIndexInfo(Map<String, Object> mapIndex, ResultSet resultSet) throws SQLException;

	/**
	 * Return count page number' sql like select count(*)...
	 *
	 * @param originalSql Original SQL
	 * @return Count page number's sql
	 */
	String getPaginationCtnSql(String originalSql);

	/**
	 * Return pagination sql like select count(*)...
	 *
	 * @param originalSql Original SQL
	 * @return Pagination number's sql
	 */
	String getPaginationSql(String originalSql, int pageNo, int perPageNum);

	/**
	 * Get detailed exception cause
	 *
	 * @param e Sql exception
	 * @return exception cause
	 */
	String getExceptionCaused(SQLException e);

	/**
	 * Return column name
	 *
	 * @param index Index
	 * @param rsmd  Result meta
	 * @return Name
	 * @throws SQLException #
	 */
	String getColumnName(int index, ResultSetMetaData rsmd) throws SQLException;

	/**
	 * Return field dml
	 *
	 * @param dataField Field
	 * @return Sql of dml
	 */
	String fieldDml(DBField dataField);

	/**
	 * @return Return db type
	 */
	String getDbType();

}
