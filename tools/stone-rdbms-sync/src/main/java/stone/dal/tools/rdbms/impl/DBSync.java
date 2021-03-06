package stone.dal.tools.rdbms.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import stone.dal.common.models.meta.ColumnInfo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.IndexMeta;
import stone.dal.common.models.meta.JoinColumn;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.dialect.MysqlDialect;
import stone.dal.jdbc.impl.dialect.OracleDialect;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.StringUtils;

public class DBSync {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @Value("${stone.dal.dialect}")
  private String dialectType;

  @Autowired(required = false)
  private DBDialectSpi dialectSpi;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private RdbmsEntityManager rdbmsEntityManager;

  @Autowired
  @Qualifier("adminJdbcTemplate")
  private JdbcTemplate adminJdbcTemplate;

  private static Logger s_logger = LoggerFactory.getLogger(DBSync.class);

  @PostConstruct
  public void init() {
    List<String> databases = adminJdbcTemplate.query("show databases", (resultSet, i) -> resultSet.getString(1));
    String dbNameInfo = getDbName();
    if (!databases.contains(dbNameInfo)) {
      syncDb(false, "db-init.sql");
    }
  }

  private String getDbName() {
    String[] dbInfos = StringUtils.splitString2Array(dbUrl, "/");
    String dbNameInfo = dbInfos[dbInfos.length - 1];
    if (dbNameInfo.contains("?")) {
      dbNameInfo = dbNameInfo.substring(0, dbNameInfo.indexOf("?"));
    }
    return dbNameInfo;
  }

  public List<ExecResult> syncDb(boolean delta, String dbScriptPath) {
    List<ExecResult> results;
    String dbNameInfo = getDbName();
    if (!delta) {
      String sql = String
          .format("DROP DATABASE IF EXISTS %s", dbNameInfo);
      adminJdbcTemplate.execute(sql);
    }
    String sql = String
        .format("CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARSET UTF8 COLLATE UTF8_GENERAL_CI", dbNameInfo);
    adminJdbcTemplate.execute(sql);
    List<String> lines = getDbScript(delta);
    results = stJdbcTemplate.execSqlScript(StringUtils.combineString(lines, ";\n"));
    if (!delta) {
      if (dbScriptPath != null) {
        results.addAll(runScript(dbScriptPath));
      }
    }
    return results;
  }

  public List<ExecResult> runScript(String dbScriptPath) {
    List<ExecResult> results = new ArrayList<>();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(dbScriptPath);
    if (is == null) {
      results.add(ExecResult.factory().error(String.format("Can not find db script %s!", dbScriptPath)).build());
    } else {
      results.addAll(stJdbcTemplate.execSqlStream(is));
    }
    return results;
  }

  public List<String> getDbInitScript() {
    List<String> lines = new ArrayList<>();
    String sql = String
        .format("CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARSET UTF8 COLLATE UTF8_GENERAL_CI", getDbName());
    lines.add(sql);
    sql = String
        .format("USE %s", getDbName());
    lines.add(sql);
    return lines;
  }

  public List<String> getDbScript(boolean delta) {
    List<String> lines = new ArrayList<>();
    DBDialectSpi dialectSpi = getDialect(dialectType);
    Assert.notNull(dialectSpi, "dialect can't be null!");
    Collection<RdbmsEntity> entities = rdbmsEntityManager.allEntities();
    entities.forEach(entity -> {
      Map<String, ColumnInfo> dbColumns = getDbColumns(dialectSpi, delta, entity.getMeta().getTableName());
      if (dbColumns != null) {
        Map<String, ColumnInfo> entityColumns = getEntityColumns(entity);
        lines.addAll(getTableDeltaSql(dialectSpi, entity, entityColumns, dbColumns));
      } else {
        //create full table script
        List<ColumnInfo> entityColumns = entity.getColumns();
        String ddl = "CREATE TABLE " + entity.getMeta().getTableName() + "(" +
            StringUtils
                .combineString(entityColumns.stream().map(columnInfo ->
                    columnInfo.getField() + " " + getDialect(dialectType).getColumnDdl(columnInfo))
                    .collect(Collectors.toList()), ",") +
            ",PRIMARY KEY(" +
            StringUtils.combineString(entity.getPks(), ",") +
            ")" +
            ")";
        lines.add(ddl);
      }
      lines.addAll(parseRelationSql(dialectSpi, entity, dbColumns, delta));
      lines.addAll(getIndicesSql(entity, delta));
    });
    return lines;
  }

  private Collection<? extends String> getTableDeltaSql(DBDialectSpi dialectSpi, RdbmsEntity entity,
      Map<String, ColumnInfo> entityColumns,
      Map<String, ColumnInfo> dbColumns) {
    List<String> lines = new ArrayList<>();
    Set<String> toDropColumns = dbColumns.keySet().stream()
        .filter(fieldName -> !entityColumns.containsKey(fieldName)
            && entity.getRelationRefDbName(fieldName) == null)
        .collect(Collectors.toSet());
    toDropColumns.forEach(dropColumnName -> {
      lines.add(String.format("ALTER TABLE %s DROP COLUMN %s", entity.getMeta().getTableName(), dropColumnName));
    });
    Set<ColumnInfo> toAddColumns = entityColumns.keySet().stream()
        .filter(fieldName -> !dbColumns.containsKey(fieldName))
        .map(entityColumns::get).collect(Collectors.toSet());
    toAddColumns.forEach(toAddColumn -> {
      lines.add(String
          .format("ALTER TABLE %s ADD %s %s", entity.getMeta().getTableName(),
              toAddColumn.getField(), dialectSpi.getColumnDdl(toAddColumn)));
    });
    entityColumns.keySet().forEach(columnName -> {
      ColumnInfo columnInfo = entityColumns.get(columnName);
      ColumnInfo dbColumnInfo = dbColumns.get(columnName);
      if (dbColumnInfo != null && !columnInfo.equals(dbColumnInfo)) {
        lines.add((String
            .format("ALTER TABLE %s MODIFY %s %s", entity.getMeta().getTableName(),
                columnInfo.getField(), dialectSpi.getColumnDdl(columnInfo))));
      }
    });
    return lines;
  }

  private Map<String, ColumnInfo> getDbColumns(DBDialectSpi dialectSpi, boolean delta, String tableName) {
    Map<String, ColumnInfo> dbColumns = null;
    List<Map> columns = null;
    if (delta) {
      dbColumns = new HashMap<>();
      try {
        SqlQueryMeta queryMeta = SqlQueryMeta.factory().sql(
            "desc " + tableName
        ).build();
        columns = stJdbcTemplate.query(queryMeta);
        for (Map column : columns) {
          ColumnInfo columnInfo = dialectSpi.getColumnInfo(column);
          dbColumns.put(columnInfo.getField(), columnInfo);
        }
      } catch (Exception ex) {
        if (ex.getMessage().contains("doesn't exist")) {
          s_logger.info(String.format("Create new table %s", tableName));
        } else {
          s_logger.error(ex.getMessage());
          throw new KernelRuntimeException(ex);
        }
      }
    }
    return dbColumns;
  }

  private List<String> parseRelationSql(DBDialectSpi dialectSpi, RdbmsEntity entity, Map<String, ColumnInfo> dbColumns,
      boolean delta) {
    List<String> lines = new ArrayList<>();
    Set<String> joinProperties = entity.getJoinProperties();
    if (!CollectionUtils.isEmpty(joinProperties)) {
      joinProperties.forEach(joinProperty -> {
        RelationMeta relation = entity.getRelMeta(joinProperty);
        if (RelationTypes.MANY_2_MANY.equals(relation.getRelationType())) {
          String joinTable = relation.getJoinTable();
          Collection<JoinColumn> joinColumns = relation.getJoinColumns();
          Collection<JoinColumn> inverseJoinColumns = relation.getInverseJoinColumns();
          List<String> joinColumnsDcl = new ArrayList<>();
          List<ColumnInfo> allColumns = new ArrayList<>();
          HashSet<String> joinTablePks = new HashSet<>();
          for (JoinColumn joinColumn : joinColumns) {
            FieldMeta fieldMeta = entity.getField(joinColumn.getReferencedColumnName());
            ColumnInfo pkColumnInfo = entity.getColumnInfo(fieldMeta.getDbName());
            joinColumnsDcl
                .add(String.format("%s %s", joinColumn.getName(), getDialect(dialectType).getColumnDdl(pkColumnInfo)));
            joinTablePks.add(joinColumn.getName());
            allColumns.add(
                new ColumnInfo(joinColumn.getName(), false, pkColumnInfo.getType(), pkColumnInfo.getProperty(), true));
          }
          RdbmsEntity relatedEntity = rdbmsEntityManager.getEntity(relation.getJoinPropertyType());
          List<String> inverseJoinColumnsDcl = new ArrayList<>();
          for (JoinColumn inverseJoinColumn : inverseJoinColumns) {
            FieldMeta fieldMeta = relatedEntity.getField(inverseJoinColumn.getReferencedColumnName());
            ColumnInfo pkColumnInfo = relatedEntity.getColumnInfo(fieldMeta.getDbName());
            inverseJoinColumnsDcl
                .add(String
                    .format("%s %s", inverseJoinColumn.getName(), getDialect(dialectType).getColumnDdl(pkColumnInfo)));
            joinTablePks.add(inverseJoinColumn.getName());
            allColumns.add(
                new ColumnInfo(inverseJoinColumn.getName(), false, pkColumnInfo.getType(), pkColumnInfo.getProperty(),
                    true));
          }
          Map<String, ColumnInfo> joinTableColumns = getDbColumns(dialectSpi, delta, joinTable);
          if (joinTableColumns != null) {
            Map<String, ColumnInfo> columnInfoMap = allColumns.stream()
                .collect(Collectors.toMap(ColumnInfo::getField, columnInfo -> columnInfo));
            lines
                .addAll(getTableDeltaSql(dialectSpi, new RdbmsEntity(EntityMeta.factory().tableName(joinTable).build()),
                    columnInfoMap, joinTableColumns));
          } else {
            String ddl = "CREATE TABLE " + joinTable + "(" +
                StringUtils.combineString(joinColumnsDcl, ",") + "," +
                StringUtils.combineString(inverseJoinColumnsDcl, ",") +
                ",PRIMARY KEY(" +
                StringUtils.combineString(joinTablePks, ",") + "))";
            lines.add(ddl);
          }
        } else if (!CollectionUtils.isEmpty(relation.getJoinColumns())) {
          Collection<JoinColumn> joinColumns = relation.getJoinColumns();
          List<String> deltaFkColumns = new ArrayList<>();
          for (JoinColumn joinColumn : joinColumns) {
            FieldMeta fieldMeta = entity.getField(joinColumn.getReferencedColumnName());
            ColumnInfo pkColumnInfo = entity.getColumnInfo(fieldMeta.getDbName());
            if (!delta || dbColumns == null || !dbColumns.containsKey(joinColumn.getName())) {
              lines.add(String.format("ALTER TABLE %s ADD %s %s", entity.getMeta().getTableName(), joinColumn.getName(),
                  getDialect(dialectType).getColumnDdl(pkColumnInfo)));
              deltaFkColumns.add(joinColumn.getName());
            }
          }
          if (!CollectionUtils.isEmpty(deltaFkColumns)) {
            String indexName = ("idx_" + relation.getJoinProperty() + "_fk").toUpperCase();
            if (delta) {
              lines.add("ALTER TABLE " + entity.getMeta().getTableName() + " DROP INDEX " + indexName);
            }
            lines.add(String.format("CREATE INDEX %s on %s (%s)", indexName,
                entity.getMeta().getTableName(),
                StringUtils.combineString(deltaFkColumns, ",")));
          }
        }
      });
    }
    return lines;
  }

  private List<String> getIndicesSql(RdbmsEntity entity, boolean delta) {
    List<String> lines = new ArrayList<>();
    //Handle unique indices
    Collection<IndexMeta> uniqueIndices = entity.getMeta().getUniqueIndices();
    for (IndexMeta index : uniqueIndices) {
      String indexName = index.getName().toUpperCase();
      if (delta) {
        String dml = "ALTER TABLE " + entity.getMeta().getTableName() + " DROP INDEX " + indexName;
        lines.add(dml);
      }
      String dml = "create unique index " + indexName + " on " + entity.getMeta().getTableName() + "("
          + StringUtils.combineString(index.getColumnNames(), ",") + ")";
      lines.add(dml);
    }
    //Handle indices
    Collection<IndexMeta> normalIndices = entity.getMeta().getIndices();
    for (IndexMeta index : normalIndices) {
      String indexName = index.getName().toUpperCase();
      if (delta) {
        String dml = "ALTER TABLE " + entity.getMeta().getTableName() + " DROP INDEX " + indexName;
        lines.add(dml);
      }
      String dml = "CREATE INDEX " + indexName + " ON " + entity.getMeta().getTableName() + "("
          + StringUtils.combineString(index.getColumnNames(), ",") + ")";
      lines.add(dml);
    }
    return lines;
  }

  private Map<String, ColumnInfo> getEntityColumns(RdbmsEntity entity) {
    List<ColumnInfo> entityColumns = entity.getColumns();
    return entityColumns.stream().collect(Collectors.toMap(ColumnInfo::getField, columnInfo -> columnInfo));
  }

  private DBDialectSpi getDialect(String dialectType) {
    if (this.dialectSpi == null) {
      if ("mysql".equalsIgnoreCase(dialectType)) {
        return new MysqlDialect(new HashMap<>());
      } else if ("oracle".equalsIgnoreCase(dialectType)) {
        return new OracleDialect(new HashMap<>());
      }
    }
    return dialectSpi;
  }

}
