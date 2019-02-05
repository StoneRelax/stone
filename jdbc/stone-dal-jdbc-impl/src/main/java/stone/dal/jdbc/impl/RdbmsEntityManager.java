package stone.dal.jdbc.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import stone.dal.common.models.EntityMetaManager;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class RdbmsEntityManager {

  protected Map<Class, RdbmsEntity> entityMapper;

  public RdbmsEntityManager(EntityMetaManager entityMetaManager) {
    this.entityMapper = entityMetaManager.getAllEntities().stream().filter(
        entityMeta -> !entityMeta.isNosql()).collect(
        Collectors.toMap(EntityMeta::getClazz, RdbmsEntity::new));
  }

  public RdbmsEntity getEntity(Class clazz) {
    Class _clazz = clazz;
    if (BaseDo.class.isAssignableFrom(clazz)) {
      while (_clazz.getSuperclass() != BaseDo.class) {
        _clazz = _clazz.getSuperclass();
      }
      return entityMapper.get(_clazz);
    }
    return null;
  }

  public RdbmsEntity build(EntityMeta meta) {
    return entityMapper.computeIfAbsent(meta.getClazz(), s -> new RdbmsEntity(meta));
  }

  public Collection<RdbmsEntity> allEntities() {
    return Collections.unmodifiableCollection(entityMapper.values());
  }

}
