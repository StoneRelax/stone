package stone.dal.rdbms.impl.dialect;

import org.apache.commons.lang.StringUtils;
import stone.dal.rdbms.api.meta.DBField;
import stone.dal.rdbms.spi.DBDialectSpi;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import static stone.dal.kernel.KernelUtils.bool_v;
import static stone.dal.kernel.KernelUtils.str_emp;

/**
 * @author fengxie
 */
public class MysqlDialect implements DBDialectSpi {
	private Map<String, String> errors;

	public MysqlDialect(Map<String, String> errors) {
		this.errors = errors;
	}

	public void interpreterColumnInfo(Map<String, Object> column, ResultSet resultSet) throws SQLException {
		column.put(COLUMN_NAME, resultSet.getString("COLUMN_NAME").toLowerCase());
		column.put(COLUMN_TYPE, resultSet.getString("TYPE_NAME").toLowerCase());
		column.put(COLUMN_SIZE, resultSet.getObject("COLUMN_SIZE"));
		column.put(DECIMAL_DIGITS, resultSet.getObject("DECIMAL_DIGITS"));
		column.put(NUM_PREC_RADIX, resultSet.getObject("NUM_PREC_RADIX"));
		column.put(NULLABLE, bool_v((Integer) resultSet.getObject("NULLABLE")));
		column.put(SQL_DATA_TYPE, resultSet.getObject("SQL_DATA_TYPE"));
	}


	public void interpreterPrimaryKeyInfo(Map<String, Object> mapPK, ResultSet resultSet) throws SQLException {
		mapPK.put(TABLE_NAME, resultSet.getObject("TABLE_NAME"));
		mapPK.put(COLUMN_NAME, resultSet.getObject("COLUMN_NAME"));
		mapPK.put(KEY_SEQUENCE, resultSet.getObject("KEY_SEQ"));
		mapPK.put(PK_NAME, resultSet.getObject("PK_NAME"));
	}

	public void interpreterIndexInfo(Map<String, Object> mapIndex, ResultSet resultSet) throws SQLException {
		mapIndex.put(TABLE_NAME, resultSet.getObject("TABLE_NAME"));
		mapIndex.put(NON_UNIQUE, resultSet.getObject("NON_UNIQUE"));
		mapIndex.put(INDEX_NAME, resultSet.getObject("INDEX_NAME"));
		mapIndex.put(INDEX_TYPE, resultSet.getObject("TYPE"));
		mapIndex.put(COLUMN_NAME, resultSet.getObject("COLUMN_NAME"));
		mapIndex.put(ASC_OR_DESC, resultSet.getObject("ASC_OR_DESC"));
	}

	@Override
	public void interpreterImportedKeyInfo(Map mapFK, ResultSet resultSet) throws SQLException {

	}

	@Override
	public void interpreterExportKeyInfo(Map mapFK, ResultSet resultSet) throws SQLException {

	}

	public String getPaginationCtnSql(String originalSql) {
		StringBuilder pagingSelect = new StringBuilder(100);
		boolean nestSql = false;
		String _originalSql = originalSql.toLowerCase();
		if (_originalSql.contains(" distinct ") || _originalSql.contains(" dist ")
				|| _originalSql.contains("( select") || _originalSql.contains("(select")
				|| _originalSql.contains("count(")
				|| _originalSql.contains("group by")) {
			nestSql = true;
		}
		if (!nestSql) {
			pagingSelect.append("select count(1) ");
			int firstFrom = _originalSql.indexOf(" from ");
			pagingSelect.append(StringUtils.substring(originalSql, firstFrom, originalSql.length()));
		} else {
			pagingSelect.append("select count(*) from ( ");
			pagingSelect.append(originalSql);
			pagingSelect.append(" ) a");
		}
		return pagingSelect.toString();
	}

	public String getPaginationSql(String originalSql, int pageNo, int perPageNum) {
		int currentRowIndex = (pageNo - 1) * perPageNum;
		return "(" + originalSql + ") limit " + currentRowIndex + "," + perPageNum;
	}

	public String getExceptionCaused(SQLException e) {
		return errors.get(String.valueOf(e.getErrorCode()));
	}

	@Override
	public String getColumnName(int index, ResultSetMetaData rsmd) throws SQLException {
		return rsmd.getColumnLabel(index);
	}

	@Override
	public String fieldDml(DBField dataField) {
		StringBuilder sb = new StringBuilder();
		String type = dataField.getType().toLowerCase();
		if (type.contains("string")) {
			sb.append("varchar");
			sb.append("(").append(dataField.getMaxlength()).append(")");
		} else if (type.contains("integer") || type.equals("int") || type.contains("long") || type.contains("long")
				|| type.contains("double")) {
			sb.append("decimal");
			String precisionScale = "";
			if (dataField.getPrecision() > 0) {
				precisionScale += dataField.getPrecision();
			}
			if (dataField.getScale() > 0) {
				if (!str_emp(precisionScale)) {
					precisionScale += ",";
				}
				precisionScale += dataField.getScale();
			}
			if (!str_emp(precisionScale)) {
				sb.append("(").append(precisionScale).append(")");
			}
		} else if (type.contains("boolean")) {
			sb.append("decimal(1)");
		} else if (type.contains("timestamp") || type.contains("datetime")) {
			sb.append("datetime");
		} else if (type.contains("time")) {
			sb.append("varchar(5)");
		} else if (type.contains("date")) {
			sb.append("date");
		}
		if (!dataField.isNullable()) {
			sb.append(" not null");
		}
		return sb.toString();
	}

	@Override
	public String getDbType() {
		return "mysql";
	}
}
