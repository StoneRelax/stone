package stone.dal.jdbc.impl;


import java.util.concurrent.ConcurrentHashMap;
import stone.dal.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class DalRdbmsEntityManager {

	protected ConcurrentHashMap<Class, RdbmsEntity> entityMapper = new ConcurrentHashMap<>();

	DalRdbmsEntityManager() {
	}

	public RdbmsEntity build(EntityMeta meta) {
		return entityMapper.computeIfAbsent(meta.getClazz(), s -> new RdbmsEntity(meta));
	}

	public static DalRdbmsEntityManager getInstance() {
		return DalRdbmsEntityManagerHolder.singleton;
	}

	private static class DalRdbmsEntityManagerHolder {
		private static DalRdbmsEntityManager singleton = new DalRdbmsEntityManager();
	}
}
