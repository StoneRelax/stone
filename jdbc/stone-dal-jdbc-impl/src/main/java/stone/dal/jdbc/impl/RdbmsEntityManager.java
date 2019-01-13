package stone.dal.jdbc.impl;

import java.util.Map;
import java.util.stream.Collectors;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class RdbmsEntityManager {

  protected Map<Class, RdbmsEntity> entityMapper;


	public RdbmsEntityManager(EntityMetaManager entityMetaManager) {
		this.entityMapper = entityMetaManager.getAllEntities().stream().collect(Collectors.toMap(
				EntityMeta::getClazz, RdbmsEntity::new));
	}

	public RdbmsEntity getEntity(Class clazz) {
		return entityMapper.get(clazz);
	}

	public RdbmsEntity build(EntityMeta meta) {
		return entityMapper.computeIfAbsent(meta.getClazz(), s -> new RdbmsEntity(meta));
	}

}
