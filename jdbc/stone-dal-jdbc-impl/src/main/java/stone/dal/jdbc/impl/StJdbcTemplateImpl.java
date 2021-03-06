package stone.dal.jdbc.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;
import stone.dal.common.spi.ResultSetClobHandler;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlBaseMeta;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.jdbc.spi.JdbcTemplateSpi;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.kernel.utils.StringUtils;

import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;
import static stone.dal.kernel.utils.KernelUtils.replace;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

/**
 * @author fengxie
 */
public class StJdbcTemplateImpl implements StJdbcTemplate {

  private ResultSetClobHandler resultSetClobHandler;

  private JdbcTemplateSpi jdbcTemplateSpi;

  private DBDialectSpi dbDialectSpi;

  private DefaultRowMapper rowMapper;

  private RdbmsEntityManager entityMetaManager;

  private static Logger s_logger = LoggerFactory.getLogger(StJdbcTemplateImpl.class);

  public StJdbcTemplateImpl(JdbcTemplateSpi jdbcTemplateSpi, DBDialectSpi dbDialectSpi,
      RelationQueryBuilder relationQueryBuilder,
      RdbmsEntityManager entityMetaManager, ResultSetClobHandler resultSetClobHandler) {
    this.dbDialectSpi = dbDialectSpi;
    this.entityMetaManager = entityMetaManager;
    this.jdbcTemplateSpi = jdbcTemplateSpi;
    this.resultSetClobHandler = resultSetClobHandler;
    this.rowMapper = new DefaultRowMapper(dbDialectSpi, entityMetaManager, relationQueryBuilder, jdbcTemplateSpi);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> query(SqlQueryMeta queryMeta) {
    if (s_logger.isInfoEnabled()) {
      s_logger.info(String.format("Query Sql:%s", queryMeta.getSql()));
      s_logger.info(String.format("Query Params:%s", StringUtils.combineString(queryMeta.getParameters(), ",")));
      //todo translate
    }
    List<T> res = jdbcTemplateSpi.query(queryMeta, this.rowMapper);
    resolveClob(res, queryMeta);
    resolveColumnMapper(res, queryMeta);
    return res;
  }

  @SuppressWarnings("unchecked")
  private <T> void resolveColumnMapper(List<T> res, SqlQueryMeta queryMeta) {
    if (queryMeta.getMappingClazz() != null) {
      RdbmsEntity entity = entityMetaManager.getEntity(queryMeta.getMappingClazz());
      if (entity != null) {
        Set<String> fields = entity.getColumnsHavingMapper();
        fields.forEach(field -> {
          RdbmsEntity.ColumnMapper columnMapper = entity.getColumnMapper(field);
          res.forEach(row -> {
            Object val = columnMapper.getDalColumnMapper()
                .map((BaseDo) row, field, columnMapper.getAssociateColumn(), columnMapper.getArgs());
            KernelUtils.setPropVal(row, field, val);
          });
        });
      }
    }
  }

  private void resolveClob(List res, SqlQueryMeta queryMeta) {
    if (queryMeta.getMappingClazz() != null) {
      RdbmsEntity entity = entityMetaManager.getEntity(queryMeta.getMappingClazz());
      if (entity != null) {
        resultSetClobHandler.handle(res, entity.getMeta());
      }
    }
  }

  public <T> List<T> query(SqlCondition condition) {
    SqlQueryMeta queryMeta = condition.build();
    RdbmsEntity entity = entityMetaManager.getEntity(queryMeta.getMappingClazz());
    String sql = entity.getFindSqlNoCondition() + " where ";
    SqlQueryMeta _queryMeta = SqlQueryMeta.factory()
        .mappingClazz(queryMeta.getMappingClazz())
        .sql(sql).join(queryMeta).build();
    return query(_queryMeta);
  }

  public <T> T queryOne(SqlCondition condition) {
    SqlQueryMeta queryMeta = condition.build();
    RdbmsEntity entity = entityMetaManager.getEntity(queryMeta.getMappingClazz());
    String sql = entity.getFindSqlNoCondition();
    if (!StringUtils.isEmpty(queryMeta.getSql())) {
      sql += " where ";
    }
    SqlQueryMeta _queryMeta = SqlQueryMeta.factory()
        .mappingClazz(queryMeta.getMappingClazz())
        .sql(sql).join(queryMeta).build();
    List<T> res = query(_queryMeta);
    if (isCollectionEmpty(res)) {
      return res.get(0);
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Page<T> pageQuery(SqlCondition condition, int pageNo, int pageSize) {
    SqlQueryMeta queryMeta = condition.build();
    RdbmsEntity entity = entityMetaManager.getEntity(queryMeta.getMappingClazz());
    String sql = entity.getFindSqlNoCondition();
    String _sql = replace(sql, "\n", " ");
    if (!StringUtils.isEmpty(queryMeta.getSql())) {
      _sql += " where ";
    }
    SqlQueryMeta _queryMeta = SqlQueryMeta.factory()
        .mappingClazz(queryMeta.getMappingClazz())
        .pageSize(pageSize).pageNo(pageNo)
        .sql(_sql).join(queryMeta).build();
    if (s_logger.isInfoEnabled()) {
      s_logger.info(String.format("Page Query Sql:%s", _queryMeta.getSql()));
      s_logger.info(String.format("Page Query Params:%s", StringUtils.combineString(_queryMeta.getParameters(), ",")));
      //todo translate
    }
    return pageQuery(_queryMeta);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Page<T> pageQuery(SqlQueryMeta queryMeta) {
    String sql = queryMeta.getSql();
    int pageNo = queryMeta.getPageNo();
    int pageSize = queryMeta.getPageSize();
    String _sql = replace(sql, "\n", " ");
    String pageQuerySql = dbDialectSpi.getPaginationSql(_sql, pageNo, pageSize);
    String pageTotalCountQuerySql = queryMeta.getPageTotalCountQuerySql();
    if (StringUtils.isEmpty(pageTotalCountQuerySql)) {
      pageTotalCountQuerySql = dbDialectSpi.getPaginationCtnSql(_sql);
    } else {
      pageTotalCountQuerySql = dbDialectSpi.getPaginationCtnSql(pageTotalCountQuerySql);
    }
    if (s_logger.isInfoEnabled()) {
      s_logger.info(String.format("Query Sql:%s", pageQuerySql));
      s_logger.info(String.format("Query Params:%s", StringUtils.combineString(queryMeta.getParameters(), ",")));
      //todo translate
    }

    SqlQueryMeta pageQueryMeta = SqlQueryMeta.bindPageSql(queryMeta, pageQuerySql, pageTotalCountQuerySql);
    Page<T> page = jdbcTemplateSpi.queryPage(pageQueryMeta, this.rowMapper);
    resolveColumnMapper(page.getRows(), queryMeta);
    return page;
  }

  @Override
  public <T> T queryOne(SqlQueryMeta queryMeta) {
    List<T> result = query(queryMeta);
    if (!isCollectionEmpty(result)) {
      return result.get(0);
    }
    return null;
  }

  @Override
  public int exec(SqlBaseMeta meta) {
    if (s_logger.isInfoEnabled()) {
      s_logger.info(String.format("Exec Sql:%s", meta.getSql()));
      s_logger.info(String.format("Exec Params:%s", StringUtils.combineString(meta.getParameters(), ",")));
      //todo translate
    }
    return jdbcTemplateSpi.exec(meta);
  }

  @Override
  public int exec(String sql) {
    if (s_logger.isInfoEnabled()) {
      s_logger.info(String.format("Exec Sql:%s", sql));
    }
    return jdbcTemplateSpi.exec(SqlBaseMeta.factory().sql(sql).build());
  }

  @Override
  public List<ExecResult> execSqlStream(InputStream inputStream) {
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
      return execSqlScript(sb.toString());
    } catch (IOException e) {
      throw new KernelRuntimeException(e);
    }
  }

  @Override
  public List<ExecResult> execSqlScript(String sqlScripts) {
    if (!sqlScripts.endsWith(";")) {
      sqlScripts += ";";
    }
    List<ExecResult> results = new ArrayList<>();
    String[] sqls = str2Arr(sqlScripts, ";");
    for (String sql : sqls) {
      if (!StringUtils.isEmpty(sql)) {
        try {
          int rows = exec(sql);
          results.add(ExecResult.factory().sql(sql).rows(rows).build());
        } catch (Exception ex) {
          results.add(ExecResult.factory().sql(sql).error(ex.getMessage()).build());
        }
      }
    }
    return results;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> queryClobKey(SqlQueryMeta queryMeta) {
    return (List<T>) jdbcTemplateSpi.query(queryMeta, this.rowMapper);
  }
}
