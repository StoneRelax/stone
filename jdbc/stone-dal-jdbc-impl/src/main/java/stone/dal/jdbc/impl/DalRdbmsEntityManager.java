package stone.dal.jdbc.impl;


import java.util.concurrent.ConcurrentHashMap;
import stone.dal.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class DalRdbmsEntityManager {

	protected ConcurrentHashMap<Class, DalRdbmsEntity> entityMapper = new ConcurrentHashMap<>();

	DalRdbmsEntityManager() {
	}

	public DalRdbmsEntity build(EntityMeta meta) {
		return entityMapper.computeIfAbsent(meta.getClazz(), s -> new DalRdbmsEntity(meta));
	}

	public static DalRdbmsEntityManager getInstance() {
		return DalRdbmsEntityManagerHolder.singleton;
	}

	private static class DalRdbmsEntityManagerHolder {
		private static DalRdbmsEntityManager singleton = new DalRdbmsEntityManager();
	}
}
