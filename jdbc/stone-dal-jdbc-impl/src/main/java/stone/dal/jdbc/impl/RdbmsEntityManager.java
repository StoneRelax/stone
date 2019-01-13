package stone.dal.jdbc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class RdbmsEntityManager {

	protected Map<Class, RdbmsEntity> entityMapper = new HashMap<>();

	public RdbmsEntityManager(EntityMetaManager entityMetaManager) {
		this.entityMapper = entityMetaManager.getAllEntities().stream().collect(Collectors.toMap(
				EntityMeta::getClazz, RdbmsEntity::new));
	}

	public RdbmsEntity getEntity(Class clazz) {
		return entityMapper.get(clazz);
	}

}
