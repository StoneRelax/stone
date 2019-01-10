package stone.dal.rdbms.impl;


import stone.dal.common.DalEntity;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.DalStates;
import stone.dal.common.api.RelationTypes;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.FieldMeta;
import stone.dal.common.api.meta.JoinColumn;
import stone.dal.common.api.meta.RelationMeta;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.rdbms.api.meta.SqlDmlDclMeta;
import stone.dal.rdbms.api.meta.SqlQueryMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static stone.dal.kernel.KernelUtils.*;

/**
 * @author fengxie
 */
public class DalRdbmsEntity extends DalEntity {

	private String insertDml;
	private String deleteDml;
	private String updateDml;
	private String findSql;
	private String findSqlNoCondition;

	private HashMap<String, FieldMeta> dbFieldNameMapper;
	private HashMap<String, String> dbFieldRelationRefMapper;
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

	public DalRdbmsEntity(EntityMeta meta) {
		super(meta);
	}

	@Override
	protected void doInit() {
		dbFieldNameMapper = new HashMap<>();
		relationMapper = new HashMap<>();
		insertRelSqls = new ConcurrentHashMap<>();
		delRelSqls = new ConcurrentHashMap<>();
		findRelSqls = new ConcurrentHashMap<>();
		dbFieldRelationRefMapper = new HashMap<>();
	}

	@Override
	protected void readEntityMeta(EntityMeta meta) {
		super.readEntityMeta(meta);
		meta.getRelations().forEach(this::readRelation);
		insertDml = buildInsertSql();
		deleteDml = buildDeleteSql();
		updateDml = buildUpdateSql();
		findSql = buildFindSql();
		findSqlNoCondition = buildSelectSql(meta.getTableName(), false);
	}

	@Override
	protected void readFieldInfo(FieldMeta fieldMeta) {
		dbFieldNameMapper.put(fieldMeta.getDbName(), fieldMeta);
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
				dbFieldRelationRefMapper.put(joinColumn.getName(), relation.getJoinProperty() + "." + joinColumn.getReferencedColumnName());
			});
		}
	}

	public String readRelType(String propertyName) {
		return relationMapper.get(propertyName).getJoinPropertyType();
	}

	public boolean isCollection(String propertyName) {
		RelationMeta relationMeta = relationMapper.get(propertyName);
		return relationMeta.getRelationType() == RelationTypes.ONE_2_MANY || relationMeta.getRelationType() == RelationTypes.MANY_2_MANY;
	}

	public FieldMeta getFieldByDbName(String dbName) {
		return dbFieldNameMapper.get(dbName);
	}

	public String getRelationRefDbName(String dbName) {
		return dbFieldRelationRefMapper.get(dbName);
	}

	public SqlDmlDclMeta getInsertMeta(DalObj obj, String dsSchema) {
		List<Object> params = new ArrayList<>();
		meta.getFields().stream().filter(field -> !bool_v(field.getNotPersist())).forEach(field -> {
			bindDmlParams(obj, field, params);
		});
		meta.getRelations().stream().filter(
				relation -> (relation.getRelationType() == RelationTypes.MANY_2_ONE
						|| relation.getRelationType() == RelationTypes.ONE_2_ONE_REF)).forEach(relation -> {
			DalObj mapper = get_v(obj, relation.getJoinProperty());
			relation.getJoinColumns().forEach(joinColumn -> {
				Object v = get_v(mapper, joinColumn.getReferencedColumnName());
				if (!joinColumn.isNullable() && v == null) {
					throw new KernelRuntimeException(relation.getJoinProperty() + "." + joinColumn.getReferencedColumnName()
							+ " can not be null!");
				}
				params.add(v);
			});
		});
		return SqlDmlDclMeta.factory().sql(insertDml).params(params.toArray(new Object[params.size()])).schema(dsSchema).build();
	}

	public SqlQueryMeta getFindMeta(DalObj obj, String dsSchema) {
		List<Object> params = new ArrayList<>();
		SqlQueryMeta.Factory factory = SqlQueryMeta.factory().one2oneCascadeFetching(true).sql(findSql).mappingClazz(meta.getClazz());
		bindPkParams(params, obj);
		factory.params(params.toArray(new Object[params.size()]));
		if (!str_emp(dsSchema)) {
			factory.schema(dsSchema);
		}
		return factory.build();
	}

	public SqlDmlDclMeta getUpdateMeta(DalObj obj, String dsSchema) {
		Set<String> changes = obj.get_changes();
		List<FieldMeta> changedFields = new ArrayList<>();
		List<String> changeFieldsName = new ArrayList<>();
		List<Object> params = new ArrayList<>();
		changes.stream().filter(changeField -> !pks.contains(changeField) && getField(changeField) != null).forEach(changeField -> {
			changedFields.add(getField(changeField));
		});
		changedFields.stream().filter(fieldMeta -> !bool_v(fieldMeta.getNotPersist())).forEach(fieldMeta -> {
			changeFieldsName.add(fieldMeta.getDbName() + "=?");
			bindDmlParams(obj, fieldMeta, params);
		});
		bindPkParams(params, obj);
		String sql = replace(updateDml, UPDATE_SET_HOLDER, list_2_str(changeFieldsName, ","));
		SqlDmlDclMeta.Factory factory = SqlDmlDclMeta.factory().sql(sql).schema(dsSchema)
				.params(params.toArray(new Object[params.size()]));
		return factory.build();
	}

	public SqlDmlDclMeta getDeleteMeta(DalObj obj, String dsSchema) {
		List<Object> params = new ArrayList<>();
		SqlDmlDclMeta.Factory factory = SqlDmlDclMeta.factory().sql(deleteDml);
		pks.forEach(pk -> {
			Object value = get_v(obj, pk);
			if (value == null) {
				throw new KernelRuntimeException(String.format("%s's %s can not be null", meta.getTableName(), pk));
			}
			params.add(value);
		});
		factory.params(params.toArray(new Object[params.size()]));
		if (!str_emp(dsSchema)) {
			factory.schema(dsSchema);
		}
		return factory.build();
	}

	public String getFindSqlNoCondition() {
		return findSqlNoCondition;
	}

	public SqlDmlDclMeta getDelJoinTableMeta(DalObj obj, String joinProperty, String dsSchema) {
		RelationMeta relation = relationMapper.get(joinProperty);
		List<Object> params = new ArrayList<>();
		relation.getJoinColumns().forEach(joinColumn -> {
			params.add(get_v(obj, joinColumn.getReferencedColumnName()));
		});
		String delSql = delRelSqls.get(relation.getJoinProperty() + DEL_REL_WHEN_DEL_SUFFIX);
		return SqlDmlDclMeta.factory().sql(delSql).params(params.toArray()).schema(dsSchema).build();
	}

	@SuppressWarnings("unchecked")
	private void bindDmlParams(DalObj obj, FieldMeta field, List params) {
		String propertyName = field.getName();
		boolean nullable = bool_v(field.getNullable());
		Object value = get_v(obj, propertyName);
		if (value == null) {
			if ((boolean.class == field.getType()
					|| Boolean.class == field.getType()) && !bool_v(field.getNullable())) {
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
		meta.getFields().stream().filter(field -> !bool_v(field.getNotPersist())).forEach(field -> {
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
				list_2_str(fields, ",") +
				") values(" +
				list_2_str(values, ",") +
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
		meta.getFields().stream().filter(field -> !bool_v(field.getNotPersist())).forEach(field -> {
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

		sb.append(list_2_str(fields, ","));
		sb.append(" from ");
		sb.append(joinSql);
		if (addPkValues) {
			sb.append(buildPkCriteriaSql());
		}
		return sb.toString();
	}

	private String buildPkCriteriaSql() {
		List<String> values = new ArrayList<>();
		pks.forEach(pk -> {
			FieldMeta field = fieldMapper.get(pk);
			values.add(meta.getTableName() + "." + field.getDbName() + "=?");
		});
		return " where " + list_2_str(values, " and ");
	}

	public String buildRelFindSql(String property, DalRdbmsEntity relEntity) {
		RelationMeta relationMeta = relationMapper.get(property);
		String res = findRelSqls.get(relationMeta.getJoinProperty());
		if (res == null) {
			String joinSql = buildJoins(property, relEntity);
			String orderByStr = "";
			if (!str_emp(relationMeta.getFetchOrder())) {
				orderByStr += " order by " + relationMeta.getFetchOrder();
			}
			res = relEntity.buildSelectSql(joinSql, false) + buildPkCriteriaSql() + " " + orderByStr;
			findRelSqls.putIfAbsent(relationMeta.getJoinProperty(), res);
		}
		return res;
	}

	private String buildJoins(String propertyName, DalRdbmsEntity relEntity) {
		RelationMeta relationMeta = relationMapper.get(propertyName);
		String mappedBy = relationMeta.getMapperBy();
		StringBuilder sb = new StringBuilder();
		if (str_emp(mappedBy)) {
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
				sb.append(list_2_str(innerJoinSqls, " and "));
				sb.append(" inner join ");
				sb.append(relEntity.getMeta().getTableName());
				sb.append(" on ");
				List<String> inverseInnerJoinSqls = new ArrayList<>();
				relationMeta.getInverseJoinColumns().forEach(joinColumn -> {
					inverseInnerJoinSqls.add(buildMany2ManyInverseInnerJoinSql(joinColumn,
							relationMeta.getJoinTable(), relEntity));
				});
				sb.append(list_2_str(inverseInnerJoinSqls, " and "));
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
													 String joinTable, DalRdbmsEntity relEntity) {
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

	private String buildInnerJoinSql(
			JoinColumn joinColumn, DalRdbmsEntity relEntity) {
		StringBuilder innerJoinExp = new StringBuilder();
		String fieldName = joinColumn.getName();
		innerJoinExp.append(this.getMeta().getTableName());
		innerJoinExp.append(".");
		innerJoinExp.append(fieldName);
		innerJoinExp.append("=");
		innerJoinExp.append(relEntity.getMeta().getTableName());
		innerJoinExp.append(".");
		innerJoinExp.append(joinColumn.getReferencedColumnName());
		return innerJoinExp.toString();
	}

	public Collection<SqlDmlDclMeta> buildMany2ManySaveMeta(
			DalObj obj, String joinProperty, String schema) {
		List<SqlDmlDclMeta> insertSqlMetaList = new ArrayList<>();
		List<SqlDmlDclMeta> delSqlMetaList = new ArrayList<>();
		Collection<DalObj> records = get_v(obj, joinProperty);
		RelationMeta relationMeta = relationMapper.get(joinProperty);
		if (records != null) {
			records.forEach(record -> {
				List<Object> params = new ArrayList<>();
				relationMeta.getJoinColumns().forEach(joinColumn -> {
					params.add(get_v(obj, joinColumn.getReferencedColumnName()));
				});
				relationMeta.getInverseJoinColumns().forEach(joinColumn -> {
					params.add(get_v(record, joinColumn.getReferencedColumnName()));
				});
				if (DalStates.DELETED == record.get_state()) {
					SqlDmlDclMeta meta = SqlDmlDclMeta.factory().sql(delRelSqls.get(joinProperty + DEL_REL_WHEN_SAVE_SUFFIX))
							.schema(schema).params(params.toArray(new Object[params.size()])).build();
					delSqlMetaList.add(meta);
				} else {
					SqlDmlDclMeta meta = SqlDmlDclMeta.factory().sql(insertRelSqls.get(joinProperty))
							.schema(schema).params(params.toArray(new Object[params.size()])).build();
					insertSqlMetaList.add(meta);
				}
			});
		}
		List<SqlDmlDclMeta> metaList = new ArrayList<>();
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
		sb.append(list_2_str(names, ","));
		sb.append(") values (");
		sb.append(list_2_str(params, ","));
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
		sb.append(list_2_str(names, " and "));
		return sb.toString();
	}

	private void bindPkParams(List<Object> params, DalObj obj) {
		pks.forEach(pk -> {
			Object value = get_v(obj, pk);
			if (value == null) {
				throw new KernelRuntimeException(String.format("%s's %s can not be null", meta.getTableName(), pk));
			}
			params.add(value);
		});
	}
}
