package stone.dal.jdbc.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import stone.dal.common.models.DalColumnMapper;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.BaseEntity;
import stone.dal.common.models.meta.ColumnInfo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.JoinColumn;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.jdbc.api.meta.SqlBaseMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.kernel.utils.ObjectUtils;
import stone.dal.kernel.utils.StringUtils;

import static stone.dal.kernel.utils.KernelUtils.getPropVal;
import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;
import static stone.dal.kernel.utils.KernelUtils.isStrEmpty;
import static stone.dal.kernel.utils.KernelUtils.list2Str;
import static stone.dal.kernel.utils.KernelUtils.replace;

/**
 * @author fengxie
 */
public class RdbmsEntity extends BaseEntity {

  private String insertDml;

  private String deleteDml;

  private String updateDml;

  private String findSql;

  private String findSqlNoCondition;

  private HashMap<String, FieldMeta> dbFieldNameMapper;

  private HashMap<String, String> dbFieldRelationRefMapper;

  private HashMap<String, ColumnMapper> columnMapperRegistry;

  private Map<String, RelationMeta> relationMapper;

  private static final String UPDATE_SET_HOLDER = "$CHANGED_FIELDS$";

  private static final String DEL_REL_WHEN_SAVE_SUFFIX = ".SAVE";

  private static final String DEL_REL_WHEN_DEL_SUFFIX = ".DEL";

  /**
   * Only support many 2 many
   */
  private ConcurrentHashMap<String, String> insertRelSqls;

  private ConcurrentHashMap<String, String> delRelSqls;

  private ConcurrentHashMap<String, String> findRelSqls;

  public RdbmsEntity(EntityMeta meta) {
    super(meta);
  }

  private static Logger s_logger = LoggerFactory.getLogger(RdbmsEntity.class);

  @Override
  protected void doInit() {
    dbFieldNameMapper = new HashMap<>();
    columnMapperRegistry = new HashMap<>();
    relationMapper = new HashMap<>();
    insertRelSqls = new ConcurrentHashMap<>();
    delRelSqls = new ConcurrentHashMap<>();
    findRelSqls = new ConcurrentHashMap<>();
    dbFieldRelationRefMapper = new HashMap<>();

    meta.getFields().forEach(field -> {
      dbFieldNameMapper.put(field.getDbName(), field);
      if (field.getColumnMapperClazz() != null) {
        columnMapperRegistry
            .put(field.getName(), new ColumnMapper(field.getColumnMapperClazz(),
                field.getAssociateColumn(), field.getColumnMapperArgs()));
      }
    });

    meta.getRelations().forEach(this::readRelation);
    insertDml = buildInsertSql();
    deleteDml = buildDeleteSql();
    updateDml = buildUpdateSql();
    findSql = buildFindSql();
    findSqlNoCondition = buildSelectSql(meta.getTableName(), false);
  }

  public Set<String> getColumnsHavingMapper() {
    return columnMapperRegistry.keySet();
  }

  public ColumnMapper getColumnMapper(String columnName) {
    return columnMapperRegistry.get(columnName);
  }

  public List<ColumnInfo> getColumns() {
    return meta.getFields().stream().filter(fieldMeta -> !fieldMeta.getNotPersist())
        .map(fieldMeta -> getColumnInfo(fieldMeta.getDbName())).collect(Collectors.toList());
  }

  public ColumnInfo getColumnInfo(String dbFieldName) {
    FieldMeta fieldMeta = dbFieldNameMapper.get(dbFieldName);
    String property = null;
    ColumnInfo.DBType type = ColumnInfo.DBType.varchar;
    if (fieldMeta.getType() == String.class) {
      property = Integer.toString(fieldMeta.getMaxLength());
    } else if (fieldMeta.getType() == Boolean.class) {
      type = ColumnInfo.DBType.decimal;
      property = "1,0";
    } else if (fieldMeta.getType() == long.class
        || fieldMeta.getType() == Long.class
        || fieldMeta.getType() == int.class
        || fieldMeta.getType() == Integer.class
        || fieldMeta.getType() == BigDecimal.class
        || fieldMeta.getType() == Float.class
        || fieldMeta.getType() == float.class
        || fieldMeta.getType() == Double.class
        || fieldMeta.getType() == double.class) {
      type = ColumnInfo.DBType.decimal;
      property = fieldMeta.getPrecision() + "," + fieldMeta.getScale();
    } else if (fieldMeta.getType() == Date.class) {
      type = ColumnInfo.DBType.date;
    } else if (fieldMeta.getType() == Timestamp.class) {
      type = ColumnInfo.DBType.datetime;
    }
    return new ColumnInfo(
        fieldMeta.getDbName(), fieldMeta.getNullable(), type,
        property, fieldMeta.getPk());
  }

  private void readRelation(RelationMeta relation) {
    relationMapper.put(relation.getJoinProperty(), relation);
    RelationTypes relationType = relation.getRelationType();
    if (RelationTypes.MANY_2_MANY == relationType) {
      insertRelSqls.put(relation.getJoinProperty(), buildMany2ManyInsertSql(relation));
      delRelSqls.put(relation.getJoinProperty() + DEL_REL_WHEN_SAVE_SUFFIX, buildMany2ManyDelSql(relation, true));
      delRelSqls.put(relation.getJoinProperty() + DEL_REL_WHEN_DEL_SUFFIX, buildMany2ManyDelSql(relation, false));
    } else if (RelationTypes.MANY_2_ONE == relationType) {
      relation.getJoinColumns().forEach(joinColumn -> {
        dbFieldRelationRefMapper
            .put(joinColumn.getName(), relation.getJoinProperty() + "." + joinColumn.getReferencedColumnName());
      });
    }
  }

  public RelationMeta getRelMeta(String propertyName) {
    return relationMapper.get(propertyName);
  }

  public boolean isCollection(String propertyName) {
    RelationMeta relationMeta = relationMapper.get(propertyName);
    return relationMeta.getRelationType() == RelationTypes.ONE_2_MANY ||
        relationMeta.getRelationType() == RelationTypes.MANY_2_MANY;
  }

  public FieldMeta getFieldByDbName(String dbFieldName) {
    return dbFieldNameMapper.get(dbFieldName);
  }

  public String getRelationRefDbName(String dbFieldName) {
    return dbFieldRelationRefMapper.get(dbFieldName);
  }

  public SqlBaseMeta getInsertMeta(BaseDo obj) {
    List<Object> params = new ArrayList<>();
    meta.getFields().stream().filter(field -> !KernelUtils.boolValue(field.getNotPersist())).forEach(field -> {
      bindDmlParams(obj, field, params);
    });
    meta.getRelations().stream().filter(
        relation -> (relation.getRelationType() == RelationTypes.MANY_2_ONE
            || relation.getRelationType() == RelationTypes.ONE_2_ONE_REF)).forEach(relation -> {
      BaseDo mapper = getPropVal(obj, relation.getJoinProperty());
      relation.getJoinColumns().forEach(joinColumn -> {
        Object v = getPropVal(mapper, joinColumn.getReferencedColumnName());
        if (!joinColumn.isNullable() && v == null) {
          throw new KernelRuntimeException(relation.getJoinProperty() + "." + joinColumn.getReferencedColumnName()
              + " can not be null!");
        }
        params.add(v);
      });
    });
    return SqlBaseMeta.factory().sql(insertDml).params(params.toArray(new Object[0]))
        .build();
  }

  public SqlQueryMeta getFindMeta(BaseDo obj, boolean pkOnly) {
    SqlQueryMeta.Factory factory = SqlQueryMeta.factory()
        .one2oneCascadeFetching(true).mappingClazz(ClassUtils.getUserClass(obj.getClass()));
    if (pkOnly) {
      factory.sql(findSql);
      factory.params(getPkValues(obj));
    } else {
      String sql = findSqlNoCondition;
      Collection<FieldMeta> fields = meta.getFields().stream().filter(fieldMeta -> !fieldMeta.getNotPersist()).collect(
          Collectors.toList());
      List<String> criteria = new ArrayList<>();
      List<Object> params = new ArrayList<>();
      fields.forEach(field -> {
        if (ObjectUtils.getPropertyValue(obj, field.getName()) != null) {
          criteria.add(field.getDbName() + "=?");
          params.add(ObjectUtils.getPropertyValue(obj, field.getName()));
        }
      });
      if (!isCollectionEmpty(params)) {
        sql += " where " + StringUtils.combineString(criteria, " and ");
      }
      factory.sql(sql).params(params.toArray(new Object[0]));
    }
    return factory.build();
  }

  public SqlBaseMeta getUpdateMeta(BaseDo obj) {
    Set<String> changes = obj.get_changes();
    List<String> changeFieldsName = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    Collection<String> pks = getPks();
    List<FieldMeta> changedFields = this.meta.getFields().stream()
        .filter(fieldMeta -> !pks.contains(fieldMeta.getName())
            && (changes.contains(fieldMeta.getName()) || !obj.check_attached())).collect(Collectors.toList());
    changedFields.stream().filter(fieldMeta -> !KernelUtils.boolValue(fieldMeta.getNotPersist())).forEach(fieldMeta -> {
      changeFieldsName.add(fieldMeta.getDbName() + "=?");
      bindDmlParams(obj, fieldMeta, params);
    });
    bindPkParams(params, obj);
    String sql = replace(updateDml, UPDATE_SET_HOLDER, list2Str(changeFieldsName, ","));
    SqlBaseMeta.Factory factory = SqlBaseMeta.factory().sql(sql)
        .params(params.toArray(new Object[0]));
    return factory.build();
  }

  public SqlBaseMeta getDeleteMeta(BaseDo obj) {
    List<Object> params = new ArrayList<>();
    SqlBaseMeta.Factory factory = SqlBaseMeta.factory().sql(deleteDml);
    Collection<String> pks = getPks();
    pks.forEach(pk -> {
      Object value = getPropVal(obj, pk);
      if (value == null) {
        throw new KernelRuntimeException(String.format("%s's %s can not be null", meta.getTableName(), pk));
      }
      params.add(value);
    });
    factory.params(params.toArray(new Object[0]));
    return factory.build();
  }

  public SqlQueryMeta getFindAllQueryMeta(BaseDo obj) {
    SqlQueryMeta.Factory factory = SqlQueryMeta.factory()
        .one2oneCascadeFetching(true).mappingClazz(meta.getClazz());
    String sql = findSqlNoCondition;
    factory.sql(sql);
    return factory.build();
  }

  public String getFindSqlNoCondition() {
    return findSqlNoCondition;
  }

  public SqlBaseMeta getDelJoinTableMeta(BaseDo obj, String joinProperty) {
    RelationMeta relation = relationMapper.get(joinProperty);
    List<Object> params = new ArrayList<>();
    relation.getJoinColumns().forEach(joinColumn -> {
      params.add(getPropVal(obj, joinColumn.getReferencedColumnName()));
    });
    String delSql = delRelSqls.get(relation.getJoinProperty() + DEL_REL_WHEN_DEL_SUFFIX);
    return SqlBaseMeta.factory().sql(delSql).params(params.toArray(new Object[0])).build();
  }

  @SuppressWarnings("unchecked")
  private void bindDmlParams(BaseDo obj, FieldMeta field, List<Object> params) {
    Collection<String> pks = getPks();
    String propertyName = field.getName();
    boolean nullable = KernelUtils.boolValue(field.getNullable());
    Object value = getPropVal(obj, propertyName);
    if (value == null) {
      if ((boolean.class == field.getType()
          || Boolean.class == field.getType()) && !KernelUtils.boolValue(field.getNullable())) {
        value = false;
      }
    }
    if (pks.contains(propertyName) || !nullable) {
      if (value == null) {
        throw new KernelRuntimeException(propertyName + " can not be null!");
      }
    }
    if (value instanceof Boolean) {
      if ((Boolean) value) {
        value = 1;
      } else {
        value = 0;
      }
    }
    if (value instanceof Enum) {
      value = ((Enum) value).ordinal();
    }
    params.add(value);
  }

  private String buildInsertSql() {
    List<String> fields = new ArrayList<>();
    List<String> values = new ArrayList<>();
    meta.getFields().stream().filter(field -> !KernelUtils.boolValue(field.getNotPersist())).forEach(field -> {
      fields.add(field.getDbName());
      values.add("?");
    });
    meta.getRelations().stream().filter(
        relation -> (relation.getRelationType() == RelationTypes.MANY_2_ONE
            || relation.getRelationType() == RelationTypes.ONE_2_ONE_REF)).forEach(relation -> {
      relation.getJoinColumns().forEach(joinColumn -> {
        fields.add(joinColumn.getName());
        values.add("?");
      });
    });
    return "insert into " + meta.getTableName() +
        " (" +
        list2Str(fields, ",") +
        ") values(" +
        list2Str(values, ",") +
        ")";
  }

  private String buildFindSql() {
    return buildSelectSql(meta.getTableName(), true);
  }

  private String buildUpdateSql() {
    return "update " + meta.getTableName() + " set " + UPDATE_SET_HOLDER +
        buildPkCriteriaSql();
  }

  private String buildDeleteSql() {
    return "delete from " + meta.getTableName() +
        buildPkCriteriaSql();
  }

  private String buildSelectSql(String joinSql, boolean addPkValues) {
    StringBuilder sb = new StringBuilder("select ");
    List<String> fields = new ArrayList<>();
    meta.getFields().stream().filter(field -> !KernelUtils.boolValue(field.getNotPersist())).forEach(field -> {
      String fieldName = field.getDbName();
      fields.add(meta.getTableName() + "." + fieldName);
    });
    meta.getRelations().stream().filter(relation ->
        (RelationTypes.MANY_2_ONE == relation.getRelationType()
            || RelationTypes.ONE_2_ONE_REF == relation.getRelationType())).forEach(relation -> {
      relation.getJoinColumns().forEach(joinColumn -> {
        fields.add(joinColumn.getName());
      });
    });

    sb.append(list2Str(fields, ","));
    sb.append(" from ");
    sb.append(joinSql);
    if (addPkValues) {
      sb.append(buildPkCriteriaSql());
    }
    return sb.toString();
  }

  private String buildPkCriteriaSql() {
    List<String> values = new ArrayList<>();
    List<FieldMeta> pks = getPkMeta();
    pks.forEach(pk -> {
      values.add(meta.getTableName() + "." + pk.getDbName() + "=?");
    });
    return " where " + list2Str(values, " and ");
  }

  public String buildRelFindSql(String property, RdbmsEntity relEntity) {
    RelationMeta relationMeta = relationMapper.get(property);
    String res = findRelSqls.get(relationMeta.getJoinProperty());
    if (res == null) {
      String joinSql = buildJoins(property, relEntity);
      String orderByStr = "";
      if (!isStrEmpty(relationMeta.getFetchOrder())) {
        orderByStr += " order by " + relationMeta.getFetchOrder();
      }
      res = relEntity.buildSelectSql(joinSql, false) + buildPkCriteriaSql() + " " + orderByStr;
      findRelSqls.putIfAbsent(relationMeta.getJoinProperty(), res);
    }
    return res;
  }

  private String buildJoins(String propertyName, RdbmsEntity relEntity) {
    RelationMeta relationMeta = relationMapper.get(propertyName);
    String mappedBy = relationMeta.getMappedBy();
    StringBuilder sb = new StringBuilder();
    if (isStrEmpty(mappedBy)) {
      sb.append(meta.getTableName());
      StringBuilder innerJoinSql = new StringBuilder();
      if (RelationTypes.MANY_2_MANY == relationMeta.getRelationType()) {
        sb.append(" inner join ");
        sb.append(relationMeta.getJoinTable());
        sb.append(" on ");
        List<String> innerJoinSqls = new ArrayList<>();
        relationMeta.getJoinColumns().forEach(joinColumn -> {
          innerJoinSqls.add(buildMany2ManyInnerJoinSql(joinColumn, relationMeta.getJoinTable()));
        });
        sb.append(list2Str(innerJoinSqls, " and "));
        sb.append(" inner join ");
        sb.append(relEntity.getMeta().getTableName());
        sb.append(" on ");
        List<String> inverseInnerJoinSqls = new ArrayList<>();
        relationMeta.getInverseJoinColumns().forEach(joinColumn -> {
          inverseInnerJoinSqls.add(buildMany2ManyInverseInnerJoinSql(joinColumn,
              relationMeta.getJoinTable(), relEntity));
        });
        sb.append(list2Str(inverseInnerJoinSqls, " and "));
      } else {
        relationMeta.getJoinColumns().forEach(joinColumn -> {
          innerJoinSql.append(buildInnerJoinSql(joinColumn, relEntity));
        });
        sb.append(" inner join ");
        sb.append(relEntity.getMeta().getTableName());
        sb.append(" on ");
        sb.append(innerJoinSql);
      }
    } else {
      sb.append(relEntity.buildJoins(mappedBy, this));
    }
    return sb.toString();
  }

  private String buildMany2ManyInnerJoinSql(JoinColumn joinColumn, String joinTable) {
    StringBuilder innerJoinExp = new StringBuilder();
    String fieldName = joinColumn.getName();
    innerJoinExp.append(meta.getTableName());
    innerJoinExp.append(".");
    innerJoinExp.append(joinColumn.getReferencedColumnName());
    innerJoinExp.append("=");
    innerJoinExp.append(joinTable);
    innerJoinExp.append(".");
    innerJoinExp.append(fieldName);
    return innerJoinExp.toString();
  }

  private String buildMany2ManyInverseInnerJoinSql(JoinColumn joinColumn,
      String joinTable, RdbmsEntity relEntity) {
    StringBuilder innerJoinExp = new StringBuilder();
    String fieldName = joinColumn.getName();
    innerJoinExp.append(joinTable);
    innerJoinExp.append(".");
    innerJoinExp.append(fieldName);
    innerJoinExp.append("=");
    innerJoinExp.append(relEntity.getMeta().getTableName());
    innerJoinExp.append(".");
    innerJoinExp.append(joinColumn.getReferencedColumnName());
    return innerJoinExp.toString();
  }

  public EntityMeta getMeta() {
    return meta;
  }

  private String buildInnerJoinSql(
      JoinColumn joinColumn, RdbmsEntity relEntity) {
    StringBuilder innerJoinExp = new StringBuilder();
    String fieldName = joinColumn.getName();
    innerJoinExp.append(meta.getTableName());
    innerJoinExp.append(".");
    innerJoinExp.append(fieldName);
    innerJoinExp.append("=");
    innerJoinExp.append(relEntity.getMeta().getTableName());
    innerJoinExp.append(".");
    innerJoinExp.append(joinColumn.getReferencedColumnName());
    return innerJoinExp.toString();
  }

  Collection<SqlBaseMeta> buildMany2ManySaveMeta(
      BaseDo obj, String joinProperty) {
    List<SqlBaseMeta> insertSqlMetaList = new ArrayList<>();
    List<SqlBaseMeta> delSqlMetaList = new ArrayList<>();
    Collection<BaseDo> records = getPropVal(obj, joinProperty);
    RelationMeta relationMeta = relationMapper.get(joinProperty);
    if (records != null) {
      records.forEach(record -> {
        List<Object> params = new ArrayList<>();
        relationMeta.getJoinColumns().forEach(joinColumn -> {
          params.add(getPropVal(obj, joinColumn.getReferencedColumnName()));
        });
        relationMeta.getInverseJoinColumns().forEach(joinColumn -> {
          params.add(getPropVal(record, joinColumn.getReferencedColumnName()));
        });
        if (BaseDo.States.Deleted == record.get_state()) {
          SqlBaseMeta meta = SqlBaseMeta.factory().sql(delRelSqls.get(joinProperty + DEL_REL_WHEN_SAVE_SUFFIX))
              .params(params.toArray(new Object[0])).build();
          delSqlMetaList.add(meta);
        } else {
          SqlBaseMeta meta = SqlBaseMeta.factory().sql(insertRelSqls.get(joinProperty))
              .params(params.toArray(new Object[0])).build();
          insertSqlMetaList.add(meta);
        }
      });
    }
    List<SqlBaseMeta> metaList = new ArrayList<>();
    metaList.addAll(delSqlMetaList);
    metaList.addAll(insertSqlMetaList);
    return Collections.unmodifiableCollection(metaList);
  }

  private String buildMany2ManyInsertSql(RelationMeta many2manyRel) {
    StringBuilder sb = new StringBuilder("insert into ");
    String joinTable = many2manyRel.getJoinTable();
    sb.append(joinTable);
    sb.append(" (");
    List<String> names = new ArrayList<>();
    List<String> params = new ArrayList<>();
    many2manyRel.getJoinColumns().forEach(joinColumn -> {
      names.add(joinColumn.getName());
      params.add("?");
    });
    many2manyRel.getInverseJoinColumns().forEach(joinColumn -> {
      names.add(joinColumn.getName());
      params.add("?");
    });
    sb.append(list2Str(names, ","));
    sb.append(") values (");
    sb.append(list2Str(params, ","));
    sb.append(")");
    return sb.toString();
  }

  private String buildMany2ManyDelSql(RelationMeta many2manyRel, boolean single) {
    String joinTable = many2manyRel.getJoinTable();
    StringBuilder sb = new StringBuilder("delete from ");
    sb.append(joinTable);
    sb.append(" where ");
    List<String> names = new ArrayList<>();
    many2manyRel.getJoinColumns().forEach(joinColumn -> {
      names.add(joinColumn.getName() + "=?");
    });
    if (single) {
      many2manyRel.getInverseJoinColumns().forEach(joinColumn -> {
        names.add(joinColumn.getName() + "=?");
      });
    }
    sb.append(list2Str(names, " and "));
    return sb.toString();
  }

  private List<FieldMeta> getPkMeta() {
    return meta.getFields().stream().filter(FieldMeta::getPk).collect(Collectors.toList());
  }

  public Set<String> getJoinProperties() {
    return Collections.unmodifiableSet(relationMapper.keySet());
  }

  private void bindPkParams(List<Object> params, BaseDo obj) {
    Collection<String> pks = getPks();
    pks.forEach(pk -> {
      Object value = getPropVal(obj, pk);
      if (value == null) {
        throw new KernelRuntimeException(String.format("%s's %s can not be null", meta.getTableName(), pk));
      }
      params.add(value);
    });
  }

  public Object[] getPkValues(Object object) {
    List<Object> params = new ArrayList<>();
    Collection<String> pks = getPks();
    pks.forEach(pk -> params.add(ObjectUtils.getPropertyValue(object, pk)));
    return params.toArray(new Object[0]);
  }

  public static class ColumnMapper {
    private DalColumnMapper dalColumnMapper;

    private String associateColumn;

    private String args;

    public ColumnMapper(Class clazz, String associateColumn, String args) {
      try {
        this.dalColumnMapper = (DalColumnMapper) clazz.newInstance();
        this.associateColumn = associateColumn;
        this.args = args;
      } catch (Exception ex) {
        s_logger.error(ex.getMessage());
      }
    }

    public String getArgs() {
      return args;
    }

    public DalColumnMapper getDalColumnMapper() {
      return dalColumnMapper;
    }

    public String getAssociateColumn() {
      return associateColumn;
    }
  }
}
