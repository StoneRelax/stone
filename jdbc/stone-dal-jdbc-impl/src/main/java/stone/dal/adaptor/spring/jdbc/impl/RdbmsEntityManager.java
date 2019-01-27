package stone.dal.adaptor.spring.jdbc.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import stone.dal.common.models.EntityMetaManager;
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
		return entityMapper.get(clazz);
	}

	public RdbmsEntity build(EntityMeta meta) {
		return entityMapper.computeIfAbsent(meta.getClazz(), s -> new RdbmsEntity(meta));
	}

	public Collection<RdbmsEntity> allEntities() {
		return Collections.unmodifiableCollection(entityMapper.values());
	}

}
