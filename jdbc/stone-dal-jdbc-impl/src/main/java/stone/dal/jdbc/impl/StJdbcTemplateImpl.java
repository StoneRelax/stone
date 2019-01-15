package stone.dal.jdbc.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.jdbc.spi.JdbcTemplateSpi;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.models.data.Page;

import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;
import static stone.dal.kernel.utils.KernelUtils.replace;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

/**
 * @author fengxie
 */
@Component
public class StJdbcTemplateImpl implements StJdbcTemplate {

  private JdbcTemplateSpi jdbcTemplateSpi;

  private DBDialectSpi dbDialectSpi;

  private DefaultRowMapper rowMapper;

  private RdbmsEntityManager entityMetaManager;

  public StJdbcTemplateImpl(JdbcTemplateSpi jdbcTemplateSpi, DBDialectSpi dbDialectSpi,
      RelationQueryBuilder relationQueryBuilder,
      RdbmsEntityManager entityMetaManager) {
    this.dbDialectSpi = dbDialectSpi;
    this.entityMetaManager = entityMetaManager;
    this.jdbcTemplateSpi = jdbcTemplateSpi;
    this.rowMapper = new DefaultRowMapper(dbDialectSpi, entityMetaManager, relationQueryBuilder, jdbcTemplateSpi);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> query(SqlQueryMeta queryMeta) {
    return jdbcTemplateSpi.query(queryMeta, this.rowMapper);
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
    String sql = entity.getFindSqlNoCondition() + " where ";
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
  public <T> Page<T> pagination(SqlQueryMeta queryMeta) {
    String sql = queryMeta.getSql();
    int pageNo = queryMeta.getPageNo();
    int pageSize = queryMeta.getPageSize();
    String pageQuerySql = replace(sql, "\n", " ");
    pageQuerySql = dbDialectSpi.getPaginationSql(pageQuerySql, pageNo, pageSize);
    SqlQueryMeta pageQueryMeta = SqlQueryMeta.bindPageSql(queryMeta, pageQuerySql);
    return jdbcTemplateSpi.queryPage(pageQueryMeta, this.rowMapper);
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
  public int execDml(SqlDmlDclMeta meta) {
    return jdbcTemplateSpi.exec(meta);
  }

  @Override
  public int execDcl(String sql) {
    return jdbcTemplateSpi.exec(SqlDmlDclMeta.factory().sql(sql).build());
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
    List<ExecResult> results = new ArrayList<>();
    String[] sqls = str2Arr(sqlScripts, ";");
    for (String sql : sqls) {
      try {
        int rows = execDcl(sql);
        results.add(ExecResult.factory().sql(sql).rows(rows).build());
      } catch (Exception ex) {
        results.add(ExecResult.factory().sql(sql).error(ex.getMessage()).build());
      }
    }
    return results;
  }
}
