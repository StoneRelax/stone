package stone.dal.rdbms.impl;


import stone.dal.common.api.meta.EntityMeta;

import java.util.concurrent.ConcurrentHashMap;

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
