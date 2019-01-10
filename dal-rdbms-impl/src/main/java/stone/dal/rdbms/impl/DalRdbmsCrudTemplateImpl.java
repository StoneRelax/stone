package stone.dal.rdbms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.DalStates;
import stone.dal.common.api.RelationTypes;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.RelationMeta;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.kernel.LogUtils;
import stone.dal.rdbms.api.DalRdbmsCrudTemplate;
import stone.dal.rdbms.api.DalRdbmsRunner;
import stone.dal.rdbms.api.meta.SqlCondition;
import stone.dal.rdbms.api.meta.SqlDmlDclMeta;
import stone.dal.rdbms.api.meta.SqlQueryMeta;
import stone.dal.rdbms.impl.utils.DalLazyLoadQueryMetaBuilder;

import java.util.Collection;
import java.util.List;

import static stone.dal.kernel.KernelUtils.get_v;
import static stone.dal.kernel.KernelUtils.set_v;

/**
 * @author fengxie
 */
public class DalRdbmsCrudTemplateImpl<T extends DalObj, K>
		implements DalRdbmsCrudTemplate<T, K> {

	protected DalRdbmsRunner dalRdbmsRunner;
	protected DalEntityMetaManager dalEntityMetaManager;
	private DalLazyLoadQueryMetaBuilder dalLazyLoadQueryMetaBuilder;
	private static Logger logger = LoggerFactory.getLogger(DalRdbmsCrudTemplateImpl.class);

	public DalRdbmsCrudTemplateImpl(DalRdbmsRunner dalRdbmsRunner,
									DalEntityMetaManager dalEntityMetaManager,
									DalLazyLoadQueryMetaBuilder dalLazyLoadQueryMetaBuilder) {
		this.dalRdbmsRunner = dalRdbmsRunner;
		this.dalEntityMetaManager = dalEntityMetaManager;
		this.dalLazyLoadQueryMetaBuilder = dalLazyLoadQueryMetaBuilder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public K create(T obj, String dsSchema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		runCreate(obj, dsSchema);
		Collection<String> pks = entity.getPks();
		if (pks.size() == 1) {
			return get_v(obj, pks.iterator().next());
		} else {
			Class pkClazz = entity.getPkClass();
			if (pkClazz != null) {
				try {
					K pk = (K) pkClazz.newInstance();
					pks.forEach(_pkName -> {
						set_v(pk, _pkName, get_v(obj, _pkName));
					});
					return pk;
				} catch (Exception e) {
					LogUtils.error(logger, e);
					throw new KernelRuntimeException(e);
				}
			}
		}
		return null;
	}

	@Override
	public void update(T dalObj, String dbSchema) {
		if (DalStates.UPDATED == dalObj.get_state()) {
			runUpdate(dalObj, dbSchema);
		}
	}

	@Override
	public void del(T pkObj, String dbSchema) {
		runDel(pkObj, dbSchema);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(T pkObj, String dbSchema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(pkObj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		DalObj res = dalRdbmsRunner.runFindOne(pkObj, dbSchema);
		if (res != null) {
			cascadeFind(entity, res, dbSchema, true);
		}
		return (T) res;
	}

	@Override
	public T findOne(SqlCondition condition) {
		return dalRdbmsRunner.runFindOne(condition, null);
	}

	@Override
	public List<T> findMany(SqlCondition condition) {
		return findMany(condition, null);
	}

	@Override
	public List<T> findMany(SqlCondition condition, String dsSchema) {
		return dalRdbmsRunner.runFindMany(condition, dsSchema);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findOne(SqlCondition condition, String dsSchema) {
		return (T) dalRdbmsRunner.<DalObj>runFindOne(condition, dsSchema);
	}

	@Override
	public K create(T obj) {
		return create(obj, null);
	}

	@Override
	public void update(T obj) {
		update(obj, null);
	}

	@Override
	public void del(T obj) {
		del(obj, null);
	}

	@Override
	public T get(T pk) {
		return get(pk, null);
	}

	@SuppressWarnings("unchecked")
	private void runCreate(DalObj obj, String dbSchema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		dalRdbmsRunner.runInsert(obj, dbSchema);
		cascadeInsert(entity, obj, dbSchema);
	}

	private void runUpdate(DalObj obj, String dbSchema) {
		if (DalStates.UPDATED == obj.get_state()) {
			EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
			DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
			SqlDmlDclMeta updateSqlMeta = entity.getUpdateMeta(obj, dbSchema);
			dalRdbmsRunner.runDml(updateSqlMeta);
			cascadeUpdate(entity, obj, dbSchema);
		} else if (DalStates.DELETED == obj.get_state()) {
			runDel(obj, dbSchema);
		} else {
			runCreate(obj, dbSchema);
		}
	}

	private void runDel(DalObj pkObj, String dbSchema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(pkObj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		cascadeDel(entity, pkObj, dbSchema);
		SqlDmlDclMeta sqlMeta = entity.getDeleteMeta(pkObj, dbSchema);
		dalRdbmsRunner.runDml(sqlMeta);
	}

	private void saveJoinTable(DalObj obj, EntityMeta meta, RelationMeta many2many, String schema) {
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		Collection<SqlDmlDclMeta> saveMetaList = entity.buildMany2ManySaveMeta(obj, many2many.getJoinProperty(), schema);
		saveMetaList.forEach(insertMeta -> dalRdbmsRunner.runDml(insertMeta));
	}

	private void cascadeInsert(DalRdbmsEntity entity, DalObj mainObj, String dbSchema) {
		EntityMeta meta = entity.getMeta();
		meta.getRelations().stream().filter(rel ->
				rel.getRelationType() == RelationTypes.ONE_2_MANY).forEach(rel -> {
			Collection<DalObj> records = get_v(mainObj, rel.getJoinProperty());
			records.forEach(record -> {
				String mapper = rel.getMapperBy();
				set_v(record, mapper, mainObj);
				runCreate(record, dbSchema);
			});
		});
		meta.getRelations().stream().filter(rel ->
				rel.getRelationType() == RelationTypes.MANY_2_MANY).forEach(rel -> saveJoinTable(mainObj, meta, rel, dbSchema));
	}

	private void cascadeUpdate(DalRdbmsEntity entity, DalObj mainObj, String dbSchema) {
		EntityMeta meta = entity.getMeta();
		meta.getRelations().stream().filter(relation ->
				(mainObj.get_changes().contains(relation.getJoinProperty())
						&&
						(relation.getRelationType() == RelationTypes.ONE_2_MANY
								|| relation.getRelationType() == RelationTypes.ONE_2_ONE_REF))).forEach(relation -> {
			String mapper = relation.getMapperBy();
			if (entity.isCollection(relation.getJoinProperty())) {
				List<DalObj> relObjs = get_v(mainObj, relation.getJoinProperty());
				relObjs.forEach(relObj -> {
					set_v(relObj, mapper, mainObj);
					runUpdate(relObj, dbSchema);
				});
			} else {
				DalObj relObj = get_v(mainObj, relation.getJoinProperty());
				set_v(relObj, mapper, mainObj);
				runUpdate(relObj, dbSchema);
			}
		});
		meta.getRelations().stream().filter(relation ->
				mainObj.get_changes().contains(relation.getJoinProperty()) &&
						relation.getRelationType() == RelationTypes.MANY_2_MANY).forEach(relation -> {
			Collection<SqlDmlDclMeta> saveMetaList = entity.buildMany2ManySaveMeta(mainObj, relation.getJoinProperty(), dbSchema);
			saveMetaList.forEach(saveMeta -> dalRdbmsRunner.runDml(saveMeta));
		});
	}

	private void cascadeDel(DalRdbmsEntity entity, DalObj mainObj, String dbSchema) {
		EntityMeta meta = entity.getMeta();
		meta.getRelations().stream().filter(relation ->
				relation.getRelationType() != RelationTypes.MANY_2_ONE
						&& relation.getRelationType() != RelationTypes.ONE_2_ONE_REF
						&& relation.getRelationType() != RelationTypes.MANY_2_MANY)
				.forEach(relation -> {
					SqlQueryMeta queryMeta = dalLazyLoadQueryMetaBuilder.buildMetaFactory(entity,
							mainObj, relation.getJoinProperty()).schema(dbSchema).build();
					EntityMeta relMeta = dalEntityMetaManager.getEntityByClazzName(relation.getJoinPropertyType());
					DalRdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);
					List<DalObj> relObjs = dalRdbmsRunner.runQuery(queryMeta);
					relObjs.forEach(relObj -> {
						cascadeDel(relEntity, relObj, dbSchema);
						SqlDmlDclMeta sqlMeta = relEntity.getDeleteMeta(relObj, dbSchema);
						dalRdbmsRunner.runDml(sqlMeta);
					});
				});
		meta.getRelations().stream().filter(relation -> relation.getRelationType() == RelationTypes.MANY_2_MANY)
				.forEach(relation -> delJoinTable(entity, mainObj, relation, dbSchema));
	}

	private void cascadeFind(DalRdbmsEntity entity, DalObj mainObj, String dbSchema, boolean propagateMany2OneQuey) {
		EntityMeta meta = entity.getMeta();
		meta.getRelations().stream().filter(
				relation -> (propagateMany2OneQuey && relation.getRelationType() == RelationTypes.MANY_2_ONE)
						|| relation.getRelationType() == RelationTypes.ONE_2_MANY
						|| relation.getRelationType() == RelationTypes.MANY_2_MANY
						|| relation.getRelationType() == RelationTypes.ONE_2_ONE_REF).forEach(relation -> {
			SqlQueryMeta queryMeta = dalLazyLoadQueryMetaBuilder.buildMetaFactory(entity,
					mainObj, relation.getJoinProperty()).schema(dbSchema).build();
			EntityMeta relMeta = dalEntityMetaManager.getEntityByClazzName(relation.getJoinPropertyType());
			DalRdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);

			List<DalObj> relObjs = dalRdbmsRunner.runQuery(queryMeta);
			if (relation.getRelationType() == RelationTypes.MANY_2_ONE
					|| relation.getRelationType() == RelationTypes.ONE_2_ONE_VAL) {
				set_v(mainObj, relation.getJoinProperty(), relObjs.get(0));
			} else {
				set_v(mainObj, relation.getJoinProperty(), relObjs);
				relObjs.forEach(relObj -> {
					cascadeFind(relEntity, relObj, dbSchema, false);
				});
			}
		});
	}

	private void delJoinTable(DalRdbmsEntity entity, DalObj mainObj, RelationMeta relation, String dbSchema) {
		SqlDmlDclMeta delJoinTblMeta = entity.getDelJoinTableMeta(mainObj, relation.getJoinProperty(), dbSchema);
		dalRdbmsRunner.runDml(delJoinTblMeta);
	}
}
