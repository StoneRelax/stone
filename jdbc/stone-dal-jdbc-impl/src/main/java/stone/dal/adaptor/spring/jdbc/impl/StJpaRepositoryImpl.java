package stone.dal.adaptor.spring.jdbc.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlBaseMeta;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.adaptor.spring.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.common.spi.ClobResolverSpi;
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

  private ClobResolverSpi clobResolverSpi;

  private static Logger logger = LoggerFactory.getLogger(StJpaRepositoryImpl.class);

  public StJpaRepositoryImpl(StJdbcTemplate jdbcTemplate,
      RdbmsEntityManager entityMetaManager,
      RelationQueryBuilder relationQueryBuilder,
      SequenceSpi sequenceSpi, ClobResolverSpi clobResolverSpi) {
    this.jdbcTemplate = jdbcTemplate;
    this.entityMetaManager = entityMetaManager;
    this.relationQueryBuilder = relationQueryBuilder;
    this.sequenceSpi = sequenceSpi;
    this.clobResolverSpi = clobResolverSpi;
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
    if (BaseDo.States.Updated == obj.get_state()) {
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
    List<T> res = jdbcTemplate.query(entity.getFindMeta(pk, true));
    T obj = null;
    if (!isCollectionEmpty(res)) {
      obj = res.get(0);
      cascadeFind(entity, obj, true);
    }
    return obj;
  }

  @Override
  public Collection<T> findList(T condition) {
    RdbmsEntity entity = entityMetaManager.getEntity(condition.getClass());
    SqlQueryMeta queryMeta = entity.getFindMeta(condition, false);
    return jdbcTemplate.query(queryMeta);
  }

  @Override
  public Page<T> pageQuery(T obj, int pageSize, int pageNo) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    SqlQueryMeta queryMeta = entity.getFindMeta(obj, false);
    SqlQueryMeta pageQueryMeta = SqlQueryMeta.factory()
        .sql(queryMeta.getSql()).params(queryMeta.getParameters())
        .pageNo(pageNo).pageSize(pageSize).build();
    return jdbcTemplate.pageQuery(pageQueryMeta);
  }

  @SuppressWarnings("unchecked")
  private void runCreate(BaseDo obj) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    bindSeqVals(obj, entity);
    bindClobs(entity, obj);
    jdbcTemplate.exec(entity.getInsertMeta(obj));
    cascadeInsert(entity, obj);
  }

  private void bindClobs(RdbmsEntity entity, BaseDo obj) {
    for (FieldMeta fieldMeta : entity.getMeta().getFields()) {
      if (fieldMeta.getClob()) {
        bindOneClobColumn(fieldMeta, obj, entity);
      }
    }
  }

  private void bindOneClobColumn(FieldMeta fieldMeta, BaseDo obj, RdbmsEntity entity) {
    if (fieldMeta.getClob()) {
      String fieldName = fieldMeta.getName();
      clobResolverSpi.create(obj, entity.getMeta(), fieldName);
    }
  }

  private void runUpdate(BaseDo obj) {
    if (BaseDo.States.Updated == obj.get_state()) {
      RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
      if (obj.check_attached()) {
        obj.get_changes().forEach(change -> {
          FieldMeta fieldMeta = entity.getField(change);
          bindOneClobColumn(fieldMeta, obj, entity);
        });
      } else {
        entity.getMeta().getFields().forEach(fieldMeta -> bindOneClobColumn(fieldMeta, obj, entity));
      }
      jdbcTemplate.exec(entity.getUpdateMeta(obj));
      cascadeUpdate(entity, obj);
    } else if (BaseDo.States.Deleted == obj.get_state()) {
      runDel(obj);
    } else {
      runCreate(obj);
    }
  }

  private void runDel(BaseDo pkObj) {
    RdbmsEntity entity = entityMetaManager.getEntity(pkObj.getClass());
    deleteClobs(entity, pkObj);
    cascadeDel(entity, pkObj);
    SqlBaseMeta sqlMeta = entity.getDeleteMeta(pkObj);
    jdbcTemplate.exec(sqlMeta);
  }

  private void deleteClobs(RdbmsEntity entity, BaseDo obj) {
    boolean clobFlag = false;
    List<FieldMeta> clobFields = new ArrayList<>();
    for (FieldMeta fieldMeta : entity.getMeta().getFields()) {
      if (fieldMeta.getClob()) {
        clobFlag = true;
        clobFields.add(fieldMeta);
      }
    }
    if (clobFlag) {
      List<T> res = jdbcTemplate.queryClobKey(entity.getFindMeta(obj, true));
      if (!isCollectionEmpty(res)) {
        obj = res.get(0);
        cascadeFind(entity, obj, true);
      }
      for (FieldMeta fieldMeta : clobFields) {
        if (fieldMeta.getClob()) {
          String fieldName = fieldMeta.getName();
          clobResolverSpi.delete(obj, entity.getMeta(), fieldName);
        }
      }
    }
  }

  private void saveJoinTable(BaseDo obj, RelationMeta many2many) {
    RdbmsEntity entity = entityMetaManager.getEntity(obj.getClass());
    Collection<SqlBaseMeta> saveMetaList = entity.buildMany2ManySaveMeta(obj, many2many.getJoinProperty());
    saveMetaList.forEach(insertMeta -> jdbcTemplate.exec(insertMeta));
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
      Collection<SqlBaseMeta> saveMetaList = entity.buildMany2ManySaveMeta(mainObj, relation.getJoinProperty());
      saveMetaList.forEach(saveMeta -> jdbcTemplate.exec(saveMeta));
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
            jdbcTemplate.exec(relEntity.getDeleteMeta(relObj));
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
    SqlBaseMeta delJoinTblMeta = entity.getDelJoinTableMeta(mainObj, relation.getJoinProperty());
    jdbcTemplate.exec(delJoinTblMeta);
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
