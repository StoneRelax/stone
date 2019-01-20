package stone.dal.adaptor.spring.jdbc.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.adaptor.spring.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.common.spi.SequenceSpi;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.LogUtils;

import static stone.dal.kernel.utils.KernelUtils.getPropVal;
import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;
import static stone.dal.kernel.utils.KernelUtils.setPropVal;

/**
 * @author fengxie
 */
public class StJpaRepositoryImpl<T extends BaseDo, K>
    implements StJpaRepository<T, K> {

  private StJdbcTemplate jdbcTemplate;

  private RdbmsEntityManager entityMetaManager;

  private RelationQueryBuilder relationQueryBuilder;

  private SequenceSpi sequenceSpi;

  private static Logger logger = LoggerFactory.getLogger(StJpaRepositoryImpl.class);

  public StJpaRepositoryImpl(StJdbcTemplate jdbcTemplate,
      RdbmsEntityManager entityMetaManager,
      RelationQueryBuilder relationQueryBuilder,
      SequenceSpi sequenceSpi) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityMetaManager = entityMetaManager;
    this.relationQueryBuilder = relationQueryBuilder;
    this.sequenceSpi = sequenceSpi;
  }

  @Override
  @SuppressWarnings("unchecked")
  public K create(T obj) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    runCreate(obj);
    Collection<String> pks = entity.getPks();
    if (pks.size() == 1) {
      return getPropVal(obj, pks.iterator().next());
    } else {
      Class pkClazz = entity.getMeta().getPkClazz();
      if (pkClazz != null) {
        try {
          K pk = (K) pkClazz.newInstance();
          pks.forEach(_pkName -> setPropVal(pk, _pkName, getPropVal(obj, _pkName)));
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
    RdbmsEntity entity = entityMetaManager.getEntity(pk.getClass());
    List<T> res = jdbcTemplate.query(entity.getFindMeta(pk));
    T obj = null;
    if (!isCollectionEmpty(res)) {
      obj = res.get(0);
      cascadeFind(entity, obj, true);
    }
    return obj;
  }

  @SuppressWarnings("unchecked")
  private void runCreate(BaseDo obj) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    bindSeqVals(obj, entity);
    jdbcTemplate.execDml(entity.getInsertMeta(obj));
    cascadeInsert(entity, obj);
  }

  private void runUpdate(BaseDo obj) {
    if (BaseDo.States.UPDATED == obj.get_state()) {
      RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
      jdbcTemplate.execDml(entity.getUpdateMeta(obj));
      cascadeUpdate(entity, obj);
    } else if (BaseDo.States.DELETED == obj.get_state()) {
      runDel(obj);
    } else {
      runCreate(obj);
    }
  }

  private void runDel(BaseDo pkObj) {
    RdbmsEntity entity = entityMetaManager.getEntity(pkObj.getClass());
    cascadeDel(entity, pkObj);
    SqlDmlDclMeta sqlMeta = entity.getDeleteMeta(pkObj);
    jdbcTemplate.execDml(sqlMeta);
  }

  private void saveJoinTable(BaseDo obj, RelationMeta many2many) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    Collection<SqlDmlDclMeta> saveMetaList = entity.buildMany2ManySaveMeta(obj, many2many.getJoinProperty());
    saveMetaList.forEach(insertMeta -> jdbcTemplate.execDml(insertMeta));
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
        rel.getRelationType() == RelationTypes.MANY_2_MANY).forEach(rel -> saveJoinTable(mainObj, rel));
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
      saveMetaList.forEach(saveMeta -> jdbcTemplate.execDml(saveMeta));
    });
  }

  private void cascadeDel(RdbmsEntity entity, BaseDo mainObj) {
    EntityMeta meta = entity.getMeta();
    meta.getRelations().stream().filter(relation ->
        relation.getRelationType() != RelationTypes.MANY_2_ONE
            && relation.getRelationType() != RelationTypes.ONE_2_ONE_REF
            && relation.getRelationType() != RelationTypes.MANY_2_MANY)
        .forEach(relation -> {
          SqlQueryMeta queryMeta = relationQueryBuilder.buildMetaFactory(entity,
              mainObj, relation.getJoinProperty()).build();
          RdbmsEntity relEntity = entityMetaManager.getEntity(relation.getJoinPropertyType());
          List<BaseDo> relObjs = jdbcTemplate.query(queryMeta);
          relObjs.forEach(relObj -> {
            cascadeDel(relEntity, relObj);
            jdbcTemplate.execDml(relEntity.getDeleteMeta(relObj));
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
      SqlQueryMeta queryMeta = relationQueryBuilder.buildMetaFactory(entity,
          mainObj, relation.getJoinProperty()).build();
      RdbmsEntity relEntity = entityMetaManager.getEntity(relation.getJoinPropertyType());
      List<BaseDo> relObjs = jdbcTemplate.query(queryMeta);
      if (relation.getRelationType() == RelationTypes.MANY_2_ONE
          || relation.getRelationType() == RelationTypes.ONE_2_ONE_VAL) {
        setPropVal(mainObj, relation.getJoinProperty(), relObjs.get(0));
      } else {
        setPropVal(mainObj, relation.getJoinProperty(), relObjs);
        relObjs.forEach(relObj -> cascadeFind(relEntity, relObj, false));
      }
    });
  }

  private void delJoinTable(RdbmsEntity entity, BaseDo mainObj, RelationMeta relation) {
    SqlDmlDclMeta delJoinTblMeta = entity.getDelJoinTableMeta(mainObj, relation.getJoinProperty());
    jdbcTemplate.execDml(delJoinTblMeta);
  }

  private void bindSeqVals(BaseDo obj, RdbmsEntity entity) {
    if (sequenceSpi != null) {
      Set<String> seqFields = entity.getSeqFields();
      for (String seqField : seqFields) {
        FieldMeta meta = entity.getField(seqField);
        Object v = getPropVal(obj, seqField);
        if (v == null) {
          v = sequenceSpi.next(obj, meta);
          setPropVal(obj, seqField, v);
        }
      }
    }
  }
}
