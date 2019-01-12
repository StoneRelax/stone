package stone.dal.jdbc.impl.dialect;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import stone.dal.jdbc.DBDialectSpi;
import stone.dal.jdbc.api.meta.DBField;

import static stone.dal.kernel.utils.KernelUtils.isStrEmpty;

/**
 * @author fengxie
 */
public class OracleDialect implements DBDialectSpi {

	private Map<String, String> errors;

	public OracleDialect(Map<String, String> errors) {
		this.errors = errors;
	}

	public void interpreterColumnInfo(Map<String, Object> mapColumn, ResultSet resultSet) throws SQLException {
		mapColumn.put(COLUMN_NAME, resultSet.getString("COLUMN_NAME").toLowerCase());
		mapColumn.put(COLUMN_TYPE, resultSet.getString("TYPE_NAME").toLowerCase());
		mapColumn.put(COLUMN_SIZE, resultSet.getObject("COLUMN_SIZE"));
		mapColumn.put(DECIMAL_DIGITS, resultSet.getObject("DECIMAL_DIGITS"));
		mapColumn.put(NUM_PREC_RADIX, resultSet.getObject("NUM_PREC_RADIX"));
		BigDecimal nullable = (BigDecimal) resultSet.getObject("NULLABLE");
		if (nullable != null) {
			mapColumn.put(NULLABLE, nullable.intValue() != 0);
		} else {
			mapColumn.put(NULLABLE, true);
		}
		mapColumn.put(SQL_DATA_TYPE, resultSet.getObject("SQL_DATA_TYPE"));
	}


	public void interpreterPrimaryKeyInfo(Map<String, Object> mapPK, ResultSet resultSet) throws SQLException {
		mapPK.put(TABLE_NAME, resultSet.getObject("TABLE_NAME"));
		mapPK.put(COLUMN_NAME, resultSet.getObject("COLUMN_NAME"));
		mapPK.put(KEY_SEQUENCE, resultSet.getObject("KEY_SEQ"));
		mapPK.put(PK_NAME, resultSet.getObject("PK_NAME"));
	}

	public void interpreterImportedKeyInfo(Map<String, Object> mapFK, ResultSet resultSet) throws SQLException {
		mapFK.put(TABLE_NAME, resultSet.getObject("PKTABLE_NAME"));
		mapFK.put(COLUMN_NAME, resultSet.getObject("FKCOLUMN_NAME"));
		mapFK.put(REF_COLUMN_NAME, resultSet.getObject("PKCOLUMN_NAME"));
	}

	public void interpreterExportKeyInfo(Map<String, Object> mapFK, ResultSet resultSet) throws SQLException {
		mapFK.put(TABLE_NAME, resultSet.getObject("FKTABLE_NAME"));
		mapFK.put(COLUMN_NAME, resultSet.getObject("FKCOLUMN_NAME"));
		mapFK.put(REF_COLUMN_NAME, resultSet.getObject("PKCOLUMN_NAME"));
	}

	public void interpreterIndexInfo(Map<String, Object> mapIndex, ResultSet resultSet) throws SQLException {
		mapIndex.put(TABLE_NAME, resultSet.getObject("TABLE_NAME"));
		mapIndex.put(NON_UNIQUE, resultSet.getObject("NON_UNIQUE"));
		mapIndex.put(INDEX_NAME, resultSet.getObject("INDEX_NAME"));
		mapIndex.put(INDEX_TYPE, resultSet.getObject("TYPE"));
		mapIndex.put(COLUMN_NAME, resultSet.getObject("COLUMN_NAME"));
		mapIndex.put(ASC_OR_DESC, resultSet.getObject("ASC_OR_DESC"));
	}

	public String getPaginationCtnSql(String originalSql) {
		StringBuilder pagingSelect = new StringBuilder(100);
		boolean nestSql = false;
		if (originalSql.contains(" distinct ") || originalSql.contains(" dist ")
				|| originalSql.contains("( select") || originalSql.contains("(select")
				|| originalSql.contains("count(")
				|| originalSql.contains("group by")) {
			nestSql = true;
		}
		if (!nestSql) {
			pagingSelect.append("select count(1) ");
			int firstFrom = originalSql.indexOf(" from ");
			pagingSelect.append(StringUtils.substring(originalSql, firstFrom, originalSql.length()));
		} else {
			pagingSelect.append("select count(*) from ( ");
			pagingSelect.append(originalSql);
			pagingSelect.append(" ) a");
		}
		return pagingSelect.toString();
	}

	public String getPaginationSql(String originalSql, int pageNo, int perPageNum) {
		int currentRowIndex = pageNo * perPageNum;
		return "select * from (select a.*, rownum rn from (" + originalSql + ") a "
				+ "where rownum <= " +
				currentRowIndex + ") where rn > " + (currentRowIndex - perPageNum);
	}

	public String getExceptionCaused(SQLException e) {
		return errors.get(String.valueOf(e.getErrorCode()));
	}

	@Override
	public String getColumnName(int index, ResultSetMetaData rsmd) throws SQLException {
		return rsmd.getColumnName(index);
	}

	@Override
	public String fieldDml(DBField dataField) {
		StringBuilder sb = new StringBuilder();
		String type = dataField.getType();
		if (type.contains("String")) {
			sb.append("varchar2");
			sb.append("(").append(dataField.getMaxlength()).append(")");
		} else if (type.contains("BigDecimal") || type.contains("Integer") || type.equals("int") || type.contains("Long") || type.contains("long")) {
			sb.append("number");
			String precisionScale = "";
			if (dataField.getPrecision() > 0) {
				precisionScale += dataField.getPrecision();
			}
			if (dataField.getScale() > 0) {
        if (!isStrEmpty(precisionScale)) {
					precisionScale += ",";
				}
				precisionScale += dataField.getScale();
			}
      if (!isStrEmpty(precisionScale)) {
				sb.append("(").append(precisionScale).append(")");
			}
		} else if (type.contains("Boolean") || type.contains("boolean")) {
			sb.append("number(1)");
		} else if (type.contains("Date") || type.contains("Timestamp") || type.equals("date") || type.equals("datetime")) {
			sb.append("date");
		}
		return sb.toString();
	}

	@Override
	public String getDbType() {
		return "oracle";
	}
}
