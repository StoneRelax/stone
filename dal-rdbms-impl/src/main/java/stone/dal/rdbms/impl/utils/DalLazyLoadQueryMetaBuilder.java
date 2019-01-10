package stone.dal.rdbms.impl.utils;

import org.springframework.util.ClassUtils;
import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.rdbms.api.meta.SqlQueryMeta;
import stone.dal.rdbms.impl.DalRdbmsEntity;
import stone.dal.rdbms.impl.DalRdbmsEntityManager;

/**
 * @author fengxie
 */
public class DalLazyLoadQueryMetaBuilder {

	private DalEntityMetaManager dalEntityMetaManager;

	public DalLazyLoadQueryMetaBuilder(DalEntityMetaManager dalEntityMetaManager) {
		this.dalEntityMetaManager = dalEntityMetaManager;
	}

	public SqlQueryMeta.Factory buildMetaFactory(Object mainObj, String propertyName) {
		EntityMeta meta = dalEntityMetaManager.getEntity(ClassUtils.getUserClass(mainObj.getClass()));
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);

		return buildMetaFactory(entity, mainObj, propertyName);
	}

	public SqlQueryMeta.Factory buildMetaFactory(DalRdbmsEntity entity, Object mainObj, String propertyName) {
		String relClazz = entity.readRelType(propertyName);
		EntityMeta relMeta = dalEntityMetaManager.getEntityByClazzName(relClazz);
		DalRdbmsEntity relEntity = DalRdbmsEntityManager.getInstance().build(relMeta);

		String sql = entity.buildRelFindSql(propertyName, relEntity);
		return SqlQueryMeta.factory().
				sql(sql).
				mappingClazz(relMeta.getClazz()).
				params(entity.getPkValues(mainObj));
	}

}
