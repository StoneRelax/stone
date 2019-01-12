package stone.dal.jdbc.impl;

import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.jdbc.api.JdbcTemplate;
import stone.dal.jdbc.api.JpaRepository;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.utils.LazyLoadQueryMetaBuilder;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.LogUtils;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.EntityMeta;
import stone.dal.models.meta.RelationMeta;
import stone.dal.models.meta.RelationTypes;

import static stone.dal.kernel.utils.KernelUtils.getPropVal;
import static stone.dal.kernel.utils.KernelUtils.setPropVal;

/**
 * @author fengxie
 */
public class JpaRepositoryImpl<T extends BaseDo, K>
    implements JpaRepository<T, K> {

  protected JdbcTemplate jdbcTemplate;

  protected EntityMetaManager entityMetaManager;

  private LazyLoadQueryMetaBuilder lazyLoadQueryMetaBuilder;

  private static Logger logger = LoggerFactory.getLogger(JpaRepositoryImpl.class);

  public JpaRepositoryImpl(JdbcTemplate jdbcTemplate,
      EntityMetaManager entityMetaManager,
      LazyLoadQueryMetaBuilder lazyLoadQueryMetaBuilder) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityMetaManager = entityMetaManager;
    this.lazyLoadQueryMetaBuilder = lazyLoadQueryMetaBuilder;
  }

  @Override
  @SuppressWarnings("unchecked")
  public K create(T obj) {
    EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
    RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
    runCreate(obj);
    Collection<String> pks = entity.getPks();
    if (pks.size() == 1) {
      return getPropVal(obj, pks.iterator().next());
    } else {
      Class pkClazz = entity.getMeta().getPkClazz();
      if (pkClazz != null) {
        try {
          K pk = (K) pkClazz.newInstance();
          pks.forEach(_pkName -> {
            setPropVal(pk, _pkName, getPropVal(obj, _pkName));
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
  public void update(T obj) {
    if (BaseDo.States.UPDATED == obj.get_state()) {
      runUpdate(obj);
    }
  }

  @Override
  public void del(T obj) {
    runDel(obj);
  }

  @Override
  public T get(T pk) {
    EntityMeta meta = entityMetaManager.getEntity(pk.getClass());
    RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
    BaseDo res = jdbcTemplate.runFindOne(pk);
    if (res != null) {
      cascadeFind(entity, res, true);
    }
    return (T) res;
  }

  @SuppressWarnings("unchecked")
  private void runCreate(BaseDo obj) {
    EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
    RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
    jdbcTemplate.runInsert(obj);
    cascadeInsert(entity, obj);
  }

  private void runUpdate(BaseDo obj) {
    if (BaseDo.States.UPDATED == obj.get_state()) {
      EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
      RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
      SqlDmlDclMeta updateSqlMeta = entity.getUpdateMeta(obj);
      jdbcTemplate.runDml(updateSqlMeta);
      cascadeUpdate(entity, obj);
    } else if (BaseDo.States.DELETED == obj.get_state()) {
      runDel(obj);
    } else {
      runCreate(obj);
    }
  }

  private void runDel(BaseDo pkObj) {
    EntityMeta meta = entityMetaManager.getEntity(pkObj.getClass());
    RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
    cascadeDel(entity, pkObj);
    SqlDmlDclMeta sqlMeta = entity.getDeleteMeta(pkObj);
    jdbcTemplate.runDml(sqlMeta);
  }

  private void saveJoinTable(BaseDo obj, EntityMeta meta, RelationMeta many2many) {
    RdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
    Collection<SqlDmlDclMeta> saveMetaList = entity.buildMany2ManySaveMeta(obj, many2many.getJoinProperty());
    saveMetaList.forEach(insertMeta -> jdbcTemplate.runDml(insertMeta));
  }

  private void cascadeInsert(RdbmsEntity entity, BaseDo mainObj) {
    EntityMeta meta = entity.getMeta();
    meta.getRelations().stream().filter(rel ->
        rel.getRelationType() == RelationTypes.ONE_2_MANY).forEach(rel -> {
      Collection<BaseDo> records = getPropVal(mainObj, rel.getJoinProperty());
      records.forEach(record -> {
        String mappedBy = rel.getMappedBy();
        setPropVal(record, mappedBy, mainObj);
        runCreate(record);
      });
    });
    meta.getRelations().stream().filter(rel ->
        rel.getRelationType() == RelationTypes.MANY_2_MANY).forEach(rel -> saveJoinTable(mainObj, meta, rel));
  }

  private void cascadeUpdate(RdbmsEntity entity, BaseDo mainObj) {
    EntityMeta meta = entity.getMeta();
    meta.getRelations().stream().filter(relation ->
        (mainObj.get_changes().contains(relation.getJoinProperty())
            &&
            (relation.getRelationType() == RelationTypes.ONE_2_MANY
                || relation.getRelationType() == RelationTypes.ONE_2_ONE_REF))).forEach(relation -> {
      String mappedBy = relation.getMappedBy();
      if (entity.isCollection(relation.getJoinProperty())) {
        List<BaseDo> relObjs = getPropVal(mainObj, relation.getJoinProperty());
        relObjs.forEach(relObj -> {
          setPropVal(relObj, mappedBy, mainObj);
          runUpdate(relObj);
        });
      } else {
        BaseDo relObj = getPropVal(mainObj, relation.getJoinProperty());
        setPropVal(relObj, mappedBy, mainObj);
        runUpdate(relObj);
      }
    });
    meta.getRelations().stream().filter(relation ->
        mainObj.get_changes().contains(relation.getJoinProperty()) &&
            relation.getRelationType() == RelationTypes.MANY_2_MANY).forEach(relation -> {
      Collection<SqlDmlDclMeta> saveMetaList = entity.buildMany2ManySaveMeta(mainObj, relation.getJoinProperty());
      saveMetaList.forEach(saveMeta -> jdbcTemplate.runDml(saveMeta));
    });
  }

  private void cascadeDel(RdbmsEntity entity, BaseDo mainObj) {
    EntityMeta meta = entity.getMeta();
    meta.getRelations().stream().filter(relation ->
        relation.getRelationType() != RelationTypes.MANY_2_ONE
            && relation.getRelationType() != RelationTypes.ONE_2_ONE_REF
            && relation.getRelationType() != RelationTypes.MANY_2_MANY)
        .forEach(relation -> {
          SqlQueryMeta queryMeta = lazyLoadQueryMetaBuilder.buildMetaFactory(entity,
              mainObj, relation.getJoinProperty()).build();
          EntityMeta relMeta = entityMetaManager.getEntityByClazzName(relation.getJoinPropertyType());
          RdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);
          List<BaseDo> relObjs = jdbcTemplate.runQuery(queryMeta);
          relObjs.forEach(relObj -> {
            cascadeDel(relEntity, relObj);
            SqlDmlDclMeta sqlMeta = relEntity.getDeleteMeta(relObj);
            jdbcTemplate.runDml(sqlMeta);
          });
        });
    meta.getRelations().stream().filter(relation -> relation.getRelationType() == RelationTypes.MANY_2_MANY)
        .forEach(relation -> delJoinTable(entity, mainObj, relation));
  }

  private void cascadeFind(RdbmsEntity entity, BaseDo mainObj, boolean propagateMany2OneQuey) {
    EntityMeta meta = entity.getMeta();
    meta.getRelations().stream().filter(
        relation -> (propagateMany2OneQuey && relation.getRelationType() == RelationTypes.MANY_2_ONE)
            || relation.getRelationType() == RelationTypes.ONE_2_MANY
            || relation.getRelationType() == RelationTypes.MANY_2_MANY
            || relation.getRelationType() == RelationTypes.ONE_2_ONE_REF).forEach(relation -> {
      SqlQueryMeta queryMeta = lazyLoadQueryMetaBuilder.buildMetaFactory(entity,
          mainObj, relation.getJoinProperty()).build();
      EntityMeta relMeta = entityMetaManager.getEntityByClazzName(relation.getJoinPropertyType());
      RdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);

      List<BaseDo> relObjs = jdbcTemplate.runQuery(queryMeta);
      if (relation.getRelationType() == RelationTypes.MANY_2_ONE
          || relation.getRelationType() == RelationTypes.ONE_2_ONE_VAL) {
        setPropVal(mainObj, relation.getJoinProperty(), relObjs.get(0));
      } else {
        setPropVal(mainObj, relation.getJoinProperty(), relObjs);
        relObjs.forEach(relObj -> {
          cascadeFind(relEntity, relObj, false);
        });
      }
    });
  }

  private void delJoinTable(RdbmsEntity entity, BaseDo mainObj, RelationMeta relation) {
    SqlDmlDclMeta delJoinTblMeta = entity.getDelJoinTableMeta(mainObj, relation.getJoinProperty());
    jdbcTemplate.runDml(delJoinTblMeta);
  }
}
