package stone.dal.jdbc.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import stone.dal.ex.CreateRowObjectException;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.aop.DoMethodFilter;
import stone.dal.jdbc.impl.aop.DoMethodInterceptor;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.jdbc.spi.JdbcTemplateSpi;
import stone.dal.kernel.utils.CGLibUtils;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.kernel.utils.DateUtils;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.models.data.Page;
import stone.dal.models.meta.FieldMeta;

import static stone.dal.kernel.utils.KernelUtils.replace;
import static stone.dal.kernel.utils.KernelUtils.setPropVal;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

/**
 * @author fengxie
 */
@Component
public class StJdbcTemplateImpl implements StJdbcTemplate {

  private JdbcTemplateSpi jdbcTemplateSpi;

  private DBDialectSpi dbDialectSpi;

  private RdbmsEntityManager entityMetaManager;

  private RelationQueryBuilder relationQueryBuilder;

  private static Logger logger = LoggerFactory.getLogger(StJdbcTemplateImpl.class);

  public StJdbcTemplateImpl(JdbcTemplateSpi jdbcTemplateSpi, DBDialectSpi dbDialectSpi,
      RelationQueryBuilder relationQueryBuilder,
      RdbmsEntityManager entityMetaManager) {
    this.jdbcTemplateSpi = jdbcTemplateSpi;
    this.dbDialectSpi = dbDialectSpi;
    this.entityMetaManager = entityMetaManager;
    this.relationQueryBuilder = relationQueryBuilder;
  }

  @Override
  public <T> List<T> query(SqlQueryMeta queryMeta) {
    return jdbcTemplateSpi.query(queryMeta);
  }

  @Override
  public <T> Page<T> pagination(SqlQueryMeta queryMeta) {
    return jdbcTemplateSpi.runPagination(queryMeta);
  }

  @Override
  public int runDml(SqlDmlDclMeta meta) {
    return jdbcTemplateSpi.exec(meta);
  }

  @Override
  public int runDcl(String sql) {
    return jdbcTemplateSpi.exec(SqlDmlDclMeta.factory().sql(sql).build());
  }

  @Override
  public List<ExecResult> runSqlStream(InputStream inputStream) {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        if (!line.startsWith("--")) {
          sb.append(line);
        }
      }
      return runSqlScript(sb.toString());
    } catch (IOException e) {
      throw new KernelRuntimeException(e);
    }
  }

  @Override
  public List<ExecResult> runSqlScript(String sqlScripts) {
    List<ExecResult> results = new ArrayList<>();
    String[] sqls = str2Arr(sqlScripts, ";");
    for (String sql : sqls) {
      try {
        int rows = runDcl(sql);
        results.add(ExecResult.factory().sql(sql).rows(rows).build());
      } catch (Exception ex) {
        results.add(ExecResult.factory().sql(sql).error(ex.getMessage()).build());
      }
    }
    return results;
  }

//  @Override
//  public <T extends BaseDo> T runFind(SqlCondition condition) {
//    List list = runFindMany(condition);
//    if (!isCollectionEmpty(list)) {
//      return (T) list.iterator().next();
//    }
//    return null;
//  }
//
//  public <T> List<T> runFindMany(SqlCondition condition) {
//		SqlQueryMeta queryMeta = condition.build();
//		EntityMeta meta = dalEntityMetaManager.getEntity(queryMeta.getMappingClazz());
//		RdbmsEntity entity = RdbmsEntityManager.getInstance().build(meta);
//		String sql = entity.getFindSqlNoCondition() + " where ";
//		SqlQueryMeta _queryMeta = SqlQueryMeta.factory()
//				.mappingClazz(queryMeta.getMappingClazz())
//				.sql(sql).join(queryMeta).build();
//		return exec(_queryMeta);
//    return null;
//  }

  public static class DefaultRowMapper implements SqlQueryMeta.RowMapper {

    private DBDialectSpi dbDialectSpi;

    private RdbmsEntityManager entityMetaManager;

    private DoMethodInterceptor.DirtyMark dirtyMarkInterceptor;

    private DoMethodInterceptor.LazyLoad lazyLoadInterceptor;

    private DoMethodFilter.DirtyMark dirtyMarkMethodFilter;

    private DoMethodFilter.LazyLoad lazyLoadMethodFilter;

    public DefaultRowMapper(DBDialectSpi dbDialectSpi,
        RdbmsEntityManager entityMetaManager,
        RelationQueryBuilder relationQueryBuilder,
        JdbcTemplateSpi jdbcTemplateSpi) {
      this.dbDialectSpi = dbDialectSpi;
      this.entityMetaManager = entityMetaManager;
      dirtyMarkInterceptor = new DoMethodInterceptor.DirtyMark(relationQueryBuilder, jdbcTemplateSpi);
      lazyLoadInterceptor = new DoMethodInterceptor.LazyLoad(relationQueryBuilder, jdbcTemplateSpi);

      dirtyMarkMethodFilter = new DoMethodFilter.DirtyMark();
      lazyLoadMethodFilter = new DoMethodFilter.LazyLoad();
    }

    private String getColumnName(Class clazz, String rsColName) {
      String columnName;
      if (clazz != Map.class) {
        RdbmsEntity entity = entityMetaManager.getEntity(clazz);
        if (entity != null) {
          FieldMeta dataFieldMeta = entity.getFieldByDbName(rsColName);
          if (dataFieldMeta != null) {
            columnName = dataFieldMeta.getName();
          } else {
            columnName = entity.getRelationRefDbName(rsColName);
          }
        } else {
          columnName = StringUtils.dbFieldName2BeanProperty(rsColName);
        }
      } else {
        columnName = StringUtils.dbFieldName2BeanProperty(rsColName);
      }
      return columnName;
    }

    private Class getRowMapClazz(SqlQueryMeta queryMeta) throws CreateRowObjectException {
      Class clazz = queryMeta.getMappingClazz();
      if (clazz != null && clazz != Map.class) {
        try {
          if (queryMeta.isUpdatable()) {
            clazz = CGLibUtils.buildProxyClass(clazz, dirtyMarkInterceptor, dirtyMarkMethodFilter);
          } else if (queryMeta.isSupportFetchMore()) {
            clazz = CGLibUtils.buildProxyClass(clazz, lazyLoadInterceptor, lazyLoadMethodFilter);
          }
        } catch (Exception e) {
          throw new CreateRowObjectException(e);
        }
      } else {
        clazz = HashMap.class;
      }
      return clazz;
    }

    @SuppressWarnings("unchecked")
    public Object mapRow(SqlQueryMeta queryMeta,
        ResultSetMetaData rsmd, int index, ResultSet rs) {
      try {
        Object rowObj = getRowMapClazz(queryMeta).newInstance();
        String colName = dbDialectSpi.getColumnName(index + 1, rsmd).toLowerCase();
        String colClassName = rsmd.getColumnClassName(index + 1);
        Class clazz = queryMeta.getMappingClazz();
        colName = getColumnName(clazz, colName);
        if (colName == null || (!queryMeta.isOne2oneCascadeFetching() && colName.contains("."))) {
          //disable one 2 one loading
          return null;
        }
        if (colClassName.
            equalsIgnoreCase("java.lang.String")) {
          setPropVal(rowObj, colName, rs.getObject(index + 1));
        } else if (colClassName.
            equalsIgnoreCase("java.lang.Integer")) {
          setPropVal(rowObj, colName, rs.getObject(index + 1));
        } else if (colClassName.
            equalsIgnoreCase("oracle.sql.TIMESTAMP")) {
          Timestamp timeValue = rs.getTimestamp(index + 1);
          setPropVal(rowObj, colName, timeValue);
        } else if (colClassName.
            equalsIgnoreCase("java.sql.Timestamp")) {
          Timestamp timeValue = rs.getTimestamp(index + 1);
          if (timeValue != null) {
            timeValue = new Timestamp(timeValue.getTime());
            Date v = timeValue;
            if ("oracle".equals(dbDialectSpi.getDbType())) {
              int type = rsmd.getColumnType(index + 1);
              if (Types.DATE == type) {
                v = DateUtils.floor(timeValue);
              }
            } else {
              if (ClassUtils.getPropertyType(rowObj, colName) == Date.class) {
                v = DateUtils.floor(timeValue);
              }
            }
            setPropVal(rowObj, colName, v);
          }
        } else if (colClassName.
            equalsIgnoreCase("java.lang.Float")) {
          setPropVal(rowObj, colName, rs.getObject(index + 1));
        } else if (colClassName.
            equalsIgnoreCase("java.lang.Double")) {
          setPropVal(rowObj, colName, rs.getObject(index + 1));
        } else if (colClassName.
            equalsIgnoreCase("java.sql.Date")) {
          Date dateValue = (Date) rs.getObject(index + 1);
          if (dateValue != null) {
            dateValue = new Date(dateValue.getTime());
            setPropVal(rowObj, colName, dateValue);
          }
        } else if (colClassName.
            equalsIgnoreCase("java.sql.Long") || colClassName.equalsIgnoreCase("java.lang.Long")) {
          setPropVal(rowObj, colName, rs.getObject(index + 1));
        } else if (colClassName.
            equalsIgnoreCase("oracle.sql.CLOB") || colClassName.equalsIgnoreCase("com.mysql.jdbc.Clob")) {
          setPropVal(rowObj, colName, getClobData(rs.getClob(index + 1)));
        } else if (colClassName.
            equalsIgnoreCase("java.math.BigDecimal")) {
          Object o = rs.getObject(index + 1);
          if (o != null) {
            int intColPrecise = rsmd.getPrecision(index + 1);
            int intColScale = rsmd.getScale(index + 1);
            boolean isMathFunc = isMathFunc(queryMeta.getSql(), index);
            if (!isMathFunc) {
              if (intColScale > 0) {
                setPropVal(rowObj, colName, rs.getBigDecimal(index + 1));
              } else {
                if (intColPrecise > 8) {
                  setPropVal(rowObj, colName, rs.getLong(index + 1));
                } else {
                  setPropVal(rowObj, colName, rs.getInt(index + 1));
                }
              }
            } else {
              setPropVal(rowObj, colName, rs.getBigDecimal(index + 1));
            }
          } else {
            setPropVal(rowObj, colName, o);
          }
        } else {
          setPropVal(rowObj, colName, rs.getString(index + 1));
        }
        return rowObj;
      } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new KernelRuntimeException(ex);
      }
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
  }
}
