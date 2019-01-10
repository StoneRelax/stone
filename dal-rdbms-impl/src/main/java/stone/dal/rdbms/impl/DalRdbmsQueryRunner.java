package stone.dal.rdbms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.DalQueryPostHandler;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.FieldMeta;
import stone.dal.kernel.ClassUtils;
import stone.dal.kernel.DateUtils;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.kernel.StringUtils;
import stone.dal.metadata.meta.Page;
import stone.dal.rdbms.api.DalRdbmsConstants;
import stone.dal.rdbms.api.meta.SqlCondition;
import stone.dal.rdbms.api.meta.SqlQueryMeta;

import java.io.BufferedReader;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static stone.dal.kernel.KernelUtils.*;

/**
 * @author fengxie
 */
public class DalRdbmsQueryRunner extends DalKernel {

	private static Logger logger = LoggerFactory.getLogger(DalRdbmsQueryRunner.class);


	@SuppressWarnings("unchecked")
	public <T> List<T> run(SqlQueryMeta queryMeta) throws KernelRuntimeException {
		PreparedStatement ps = null;
		Connection conn = getConnection(queryMeta.getSchema());
		try {
			ps = buildStatement(conn, queryMeta.getSql());
			initParams(ps, queryMeta.getParameters());
			List resultSet = runQuery(queryMeta, ps);
			logger.info("Return {} records", resultSet.size());
			return resultSet;
		} catch (Exception e) {
			throw wrapException(queryMeta.getSchema(), e);
		} finally {
			release(queryMeta.getSchema(), ps);
			release(queryMeta.getSchema(), conn);
		}
	}


	@SuppressWarnings("unchecked")
	public <T> Page<T> runPagination(SqlQueryMeta queryMeta) throws KernelRuntimeException {
		PreparedStatement ps = null;
		PreparedStatement paginationStatement = null;
		Connection conn = getConnection(queryMeta.getSchema());
		int pageSize = queryMeta.getPageSize();
		try {
			ps = buildPaginationStatement(conn, queryMeta);
			paginationStatement = buildPaginationCtnStatement(conn, queryMeta);
			initParams(ps, queryMeta.getParameters());
			initParams(paginationStatement, queryMeta.getParameters());
			Page page = runPaginationQuery(queryMeta, ps,
					paginationStatement, pageSize);
			logger.info("Return {} records.pageNo is {}. pageSize is {}",
					page.getRows().size(), page.getPageNo(), pageSize);
			return page;
		} catch (Exception e) {
			throw wrapException(queryMeta.getSchema(), e);
		} finally {
			release(queryMeta.getSchema(), ps);
			release(queryMeta.getSchema(), paginationStatement);
			release(queryMeta.getSchema(), conn);
		}
	}

	private Page runPaginationQuery(SqlQueryMeta queryMeta,
									PreparedStatement ps,
									PreparedStatement paginationStatement,
									int pageSize) throws Exception {
		Page pageInfo = new Page();
		List resultSet = pageInfo.getRows();
		logger.info("SQL:{}", queryMeta.getSql().toUpperCase());
		logger.info("PARAMETERS:[{}]", arr_2_str(queryMeta.getParameters(), ","));
		ResultSet rsPageNum = paginationStatement.executeQuery();
		int pageNo = queryMeta.getPageNo();
		if (rsPageNum.next()) {
			int rowNums = rsPageNum.getBigDecimal(1).intValue();
			pageInfo.setTotalCount(rowNums);
			int pageNums = (rowNums + pageSize - 1) / pageSize;
			rsPageNum.close();
			if (rowNums == 0) {
				pageInfo.setPageNo(0);
				pageInfo.setTotal(0);
				return pageInfo;  //empty result set
			}
			if (pageNo > pageNums) {
				pageNo = pageNums;
			}
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				int iIndexForList = 0;
				while (rs.next() && (iIndexForList < pageSize)) {
					processResultSet(queryMeta, numberOfColumns, rs, rsmd, resultSet);
					iIndexForList++;
				}
			}
			pageInfo.setPageNo(pageNo);
			pageInfo.setTotal(pageNums);
		}
		return pageInfo;
	}


	@SuppressWarnings("unchecked")
	private List runQuery(SqlQueryMeta queryMeta, PreparedStatement ps) throws Exception {
		int rowNumCount = 0;
		List resultSet = new ArrayList();
		if (logger.isInfoEnabled()) {
			logger.info("SQL:{}", queryMeta.getSql().toUpperCase());
			logger.info("PARAMETERS:[{}]", arr_2_str(queryMeta.getParameters(), ","));
		}
		if (queryMeta.getMaxSize() > 0) {
			ps.setMaxRows(queryMeta.getMaxSize());
		}
		try (ResultSet rs = ps.executeQuery()) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			while (rs.next()) {
				processResultSet(queryMeta, numberOfColumns, rs, rsmd, resultSet);
				rowNumCount++;
				if (queryMeta.getMaxSize() > 0 && rowNumCount > queryMeta.getMaxSize()) {
					break;
				}
			}
		}
		if (queryMeta.getPostHandlers() != null) {
			DalQueryPostHandler[] callbacks = queryMeta.getPostHandlers();
			for (DalQueryPostHandler logic : callbacks) {
				logic.readRows(resultSet);
			}
		}
		return resultSet;
	}

	@SuppressWarnings("unchecked")
	private void processResultSet(
			SqlQueryMeta queryMeta, int numberOfColumns,
			ResultSet rs, ResultSetMetaData rsmd, List resultSet) throws Exception {
		Object rowObj = buildRowObj(queryMeta);
		resultSet.add(rowObj);
		for (int i = 0; i < numberOfColumns; i++) {
			bind2Obj(queryMeta, rsmd, i, rowObj, rs);
		}
		if (queryMeta.getPostHandlers() != null) {
			DalQueryPostHandler[] callbacks = queryMeta.getPostHandlers();
			for (DalQueryPostHandler callback : callbacks) {
				callback.readRow(rowObj);
			}
		}
		if (queryMeta.isUpdatable()) {
			if (rowObj instanceof DalObj) {
				((DalObj) rowObj).monitor_on();
			}
		}
	}


	private PreparedStatement buildStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(
				sql.toUpperCase(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	private PreparedStatement buildPaginationCtnStatement(
			Connection conn, SqlQueryMeta queryMeta) throws SQLException {
		String sql = queryMeta.getSql();
		int pageNo = queryMeta.getPageNo();
		int pageSize = queryMeta.getPageSize();
		String strSql = org.apache.commons.lang.StringUtils.replace(sql, "\n", " ");
		strSql = getDialect(queryMeta.getSchema()).getPaginationCtnSql(strSql);
		return conn.prepareStatement(
				strSql.toUpperCase(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	private PreparedStatement buildPaginationStatement(
			Connection conn, SqlQueryMeta queryMeta) throws SQLException {
		String sql = queryMeta.getSql();
		int pageNo = queryMeta.getPageNo();
		int pageSize = queryMeta.getPageSize();
		String strSql = org.apache.commons.lang.StringUtils.replace(sql, "\n", " ");
		strSql = getDialect(queryMeta.getSchema()).getPaginationSql(strSql, pageNo, pageSize);
		return conn.prepareStatement(
				strSql.toUpperCase(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	private String getColumnName(Class clazz, String rsColName) {
		String columnName = rsColName;
		if (dalEntityMetaManager != null && clazz != Map.class) {
			EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
			if (entityMeta != null) {
				DalRdbmsEntity dbmsEntity = DalRdbmsEntityManager.getInstance().build(entityMeta);
				FieldMeta dataFieldMeta = dbmsEntity.getFieldByDbName(rsColName);
				if (dataFieldMeta != null) {
					columnName = dataFieldMeta.getName();
				} else {
					columnName = dbmsEntity.getRelationRefDbName(rsColName);
				}
			} else {
				columnName =StringUtils.dbFieldName2BeanProperty(rsColName);
			}
		} else if (clazz != null) {
			columnName = StringUtils.dbFieldName2BeanProperty(rsColName);
		}
		return columnName;
	}


	@SuppressWarnings("unchecked")
	private void bind2Obj(SqlQueryMeta queryMeta,
						  ResultSetMetaData rsmd, int index,
						  Object rowObj, ResultSet rs) throws Exception {
		String colName = getDialect(queryMeta.getSchema()).getColumnName(index + 1, rsmd).toLowerCase();
		String colClassName = rsmd.getColumnClassName(index + 1);
		Class clazz = queryMeta.getMappingClazz();
		colName = getColumnName(clazz, colName);
		if (colName == null || (!queryMeta.isOne2oneCascadeFetching() && colName.contains("."))) {
			//disable one 2 one loading
			return;
		}
		if (colClassName.
				equalsIgnoreCase("java.lang.String")) {
			bindValue(rowObj, colName, rs.getObject(index + 1));
		} else if (colClassName.
				equalsIgnoreCase("java.lang.Integer")) {
			bindValue(rowObj, colName, rs.getObject(index + 1));
		} else if (colClassName.
				equalsIgnoreCase("oracle.sql.TIMESTAMP")) {
			Timestamp timeValue = rs.getTimestamp(index + 1);
			bindValue(rowObj, colName, timeValue);
		} else if (colClassName.
				equalsIgnoreCase("java.sql.Timestamp")) {
			Timestamp timeValue = rs.getTimestamp(index + 1);
			if (timeValue != null) {
				timeValue = new Timestamp(timeValue.getTime());
				Date v = timeValue;
				if ("oracle".equals(getType(queryMeta.getSchema()))) {
					int type = rsmd.getColumnType(index + 1);
					if (Types.DATE == type) {
						v = DateUtils.floor(timeValue);
					}
				} else {
					if (ClassUtils.getPropertyType(rowObj, colName) == Date.class) {
						v = DateUtils.floor(timeValue);
					}
				}
				bindValue(rowObj, colName, v);
			}
		} else if (colClassName.
				equalsIgnoreCase("java.lang.Float")) {
			bindValue(rowObj, colName, rs.getObject(index + 1));
		} else if (colClassName.
				equalsIgnoreCase("java.lang.Double")) {
			bindValue(rowObj, colName, rs.getObject(index + 1));
		} else if (colClassName.
				equalsIgnoreCase("java.sql.Date")) {
			Date dateValue = (Date) rs.getObject(index + 1);
			if (dateValue != null) {
				dateValue = new Date(dateValue.getTime());
				bindValue(rowObj, colName, dateValue);
			}
		} else if (colClassName.
				equalsIgnoreCase("java.sql.Long") || colClassName.equalsIgnoreCase("java.lang.Long")) {
			bindValue(rowObj, colName, rs.getObject(index + 1));
		} else if (colClassName.
				equalsIgnoreCase("oracle.sql.CLOB") || colClassName.equalsIgnoreCase("com.mysql.jdbc.Clob")) {
			bindValue(rowObj, colName, getClobData(rs.getClob(index + 1)));
		} else if (colClassName.
				equalsIgnoreCase("java.math.BigDecimal")) {
			Object o = rs.getObject(index + 1);
			if (o != null) {
				int intColPrecise = rsmd.getPrecision(index + 1);
				int intColScale = rsmd.getScale(index + 1);
				boolean isMathFunc = isMathFunc(queryMeta.getSql(), index);
				if (!isMathFunc) {
					if (intColScale > 0) {
						bindValue(rowObj, colName, rs.getBigDecimal(index + 1));
					} else {
						if (intColPrecise > 8) {
							bindValue(rowObj, colName, rs.getLong(index + 1));
						} else {
							bindValue(rowObj, colName, rs.getInt(index + 1));
						}
					}
				} else {
					bindValue(rowObj, colName, rs.getBigDecimal(index + 1));
				}
			} else {
				bindValue(rowObj, colName, o);
			}
		} else {
			bindValue(rowObj, colName, rs.getString(index + 1));
		}
	}

	private void bindValue(Object rowObj, String propertyName, Object v) {
		if (propertyName.contains(".")) {
			set_v_recursive(rowObj, propertyName, v);
		} else {
			set_v(rowObj, propertyName, v);
		}
	}

	public <T extends DalObj> T runFind(DalObj obj) {
		return runFind(obj, DalRdbmsConstants.PRIMARY_SCHEMA);
	}

	@SuppressWarnings("unchecked")
	public <T extends DalObj> T runFind(DalObj obj, String schema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		SqlQueryMeta sqlQueryMeta = entity.getFindMeta(obj, schema);
		List list = run(sqlQueryMeta);
		if (!list_emp(list)) {
			return (T) list.iterator().next();
		}
		return null;
	}

	public <T extends DalObj> T runFind(SqlCondition condition, String schema) {
		List list = runFindMany(condition, schema);
		if (!list_emp(list)) {
			return (T) list.iterator().next();
		}
		return null;
	}

	public <T> List<T> runFindMany(SqlCondition condition, String schema) {
		SqlQueryMeta queryMeta = condition.build();
		EntityMeta meta = dalEntityMetaManager.getEntity(queryMeta.getMappingClazz());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		String sql = entity.getFindSqlNoCondition() + " where ";
		SqlQueryMeta _queryMeta = SqlQueryMeta.factory().schema(schema)
				.mappingClazz(queryMeta.getMappingClazz())
				.sql(sql).join(queryMeta).build();
		return run(_queryMeta);
	}


	private boolean isMathFunc(String strSql, int fieldIndex) {
		String strFieldName;
		boolean isMathCal = false;
		String sqlToParse = replace(strSql.toLowerCase(), "\n", " ");
		sqlToParse = sqlToParse.substring(sqlToParse.indexOf("select") + 6, sqlToParse.indexOf(" from "));
		strFieldName = sqlToParse;
		while (fieldIndex >= 0) {
			sqlToParse = sqlToParse.trim();
			if (sqlToParse.contains(",")) {
				strFieldName = sqlToParse.substring(0, sqlToParse.indexOf(","));
				sqlToParse = sqlToParse.substring(sqlToParse.indexOf(",") + 1, sqlToParse.length());
			} else {
				strFieldName = sqlToParse;
			}
			fieldIndex--;
		}

		if (strFieldName.trim().contains("sum(")) {
			isMathCal = true;
		} else if (strFieldName.trim().contains("min(")) {
			isMathCal = true;
		} else if (strFieldName.trim().contains("max(")) {
			isMathCal = true;
		}
		return isMathCal;
	}

	private String getClobData(Clob clobData) throws Exception {
		StringBuilder strBuffer = new StringBuilder();
		if (clobData == null) {
			return "";
		}
		String lineData;
		BufferedReader in = new BufferedReader(clobData.getCharacterStream());
		while ((lineData = in.readLine()) != null) {
			strBuffer.append(lineData);
			strBuffer.append("\n");
		}
		return strBuffer.toString();
	}

	private Object buildRowObj(SqlQueryMeta queryMeta) throws Exception {
		if (rdbmsResultSetHandler != null) {
			return rdbmsResultSetHandler.buildRowObj(queryMeta);
		}
		Object rowObj;
		if (queryMeta.getMappingClazz() != null) {
			rowObj = queryMeta.getMappingClazz().newInstance();
		} else {
			rowObj = new HashMap();
		}
		return rowObj;
	}

	private String getClassName(ResultSetMetaData rsmd, int i) throws SQLException {
		String colClassName = rsmd.getColumnClassName(i + 1);
		if (colClassName.
				equalsIgnoreCase("java.sql.Timestamp")) {
			colClassName = "java.util.Date";
		} else if (colClassName.
				equalsIgnoreCase("java.sql.Date")) {
			colClassName = "java.util.Date";
		} else if (colClassName.
				equalsIgnoreCase("oracle.sql.CLOB")) {
			colClassName = "java.util.String";
		} else if (colClassName.
				equalsIgnoreCase("java.math.BigDecimal")) {
			int intColPrecise = rsmd.getPrecision(i + 1);
			int intColScale = rsmd.getScale(i + 1);
			boolean needMathCal = (intColScale == 0) && (intColPrecise == 0);
			if (!needMathCal) {
				if (intColScale == 0) {
					if (intColPrecise > 8) {
						colClassName = "java.lang.Long";
					} else {
						colClassName = "java.lang.Integer";
					}
				}
			}
		}
		return colClassName;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory extends DalKernel.Factory<DalRdbmsQueryRunner> {
		@Override
		protected DalRdbmsQueryRunner initRunner() {
			return new DalRdbmsQueryRunner();
		}

		public DalRdbmsQueryRunner getRunner() {
			return runner;
		}
	}
}
