package stone.dal.adaptor.spring.jdbc.impl.dialect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import stone.dal.adaptor.spring.jdbc.spi.DBDialectSpi;
import stone.dal.common.models.meta.ColumnInfo;

/**
 * @author fengxie
 */
public class MysqlDialect implements DBDialectSpi {
  private Map<String, String> errors;

  public MysqlDialect(Map<String, String> errors) {
    this.errors = errors;
  }

  @Override
  public ColumnInfo getColumnInfo(Map<String, Object> column) {
    String typeStr = (String) column.get("type");
    String nullStr = (String) column.get("null");
    String key = (String) column.get("key");
    String field = (String) column.get("field");
    String property = null;
    if (typeStr.contains("(")) {
      property = StringUtils.replace(typeStr.substring(typeStr.indexOf("(")), ")", "");
      property = StringUtils.replace(property, "(", "");
    }
    String type = StringUtils.replace(typeStr, "(" + property + ")", "");
    return new ColumnInfo(field, "YES".equalsIgnoreCase(nullStr),
        ColumnInfo.DBType.valueOf(type), property, "PRI".equalsIgnoreCase(key));
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
  public String getColumnDdl(ColumnInfo columnInfo) {
    StringBuilder sb = new StringBuilder();
    if ("datetime".equalsIgnoreCase(columnInfo.getType().name())
        || "date".equalsIgnoreCase(columnInfo.getType().name())) {
      sb.append(String.format("%s", columnInfo.getType().name()));
    } else {
      sb.append(String.format("%s(%s)", columnInfo.getType().name(), columnInfo.getProperty()));
    }
    if (!columnInfo.getNullable()) {
      sb.append(" not null");
    }
    return sb.toString();
  }

  @Override
  public String getDbType() {
    return "mysql";
  }
}
