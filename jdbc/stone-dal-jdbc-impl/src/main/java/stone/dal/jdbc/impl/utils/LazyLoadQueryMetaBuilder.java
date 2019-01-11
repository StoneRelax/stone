package stone.dal.jdbc.impl.utils;

import org.springframework.util.ClassUtils;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.DalRdbmsEntity;
import stone.dal.jdbc.impl.DalRdbmsEntityManager;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.meta.EntityMeta;

/**
 * @author fengxie
 */
public class LazyLoadQueryMetaBuilder {

	private EntityMetaManager entityMetaManager;

	public LazyLoadQueryMetaBuilder(EntityMetaManager entityMetaManager) {
		this.entityMetaManager = entityMetaManager;
	}

	public SqlQueryMeta.Factory buildMetaFactory(Object mainObj, String propertyName) {
		EntityMeta meta = entityMetaManager.getEntity(ClassUtils.getUserClass(mainObj.getClass()));
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);

		return buildMetaFactory(entity, mainObj, propertyName);
	}

	public SqlQueryMeta.Factory buildMetaFactory(DalRdbmsEntity entity, Object mainObj, String propertyName) {
		String relClazz = entity.readRelType(propertyName);
		EntityMeta relMeta = entityMetaManager.getEntityByClazzName(relClazz);
		DalRdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);

		String sql = entity.buildRelFindSql(propertyName, relEntity);
		return SqlQueryMeta.factory().
				sql(sql).
				mappingClazz(relMeta.getClazz()).
				params(entity.getPkValues(mainObj));
	}

}
