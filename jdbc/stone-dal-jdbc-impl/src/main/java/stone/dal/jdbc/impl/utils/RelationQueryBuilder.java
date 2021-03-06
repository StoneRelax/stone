package stone.dal.jdbc.impl.utils;

import org.springframework.util.ClassUtils;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;

/**
 * @author fengxie
 */
public class RelationQueryBuilder {

	private RdbmsEntityManager entityManager;

	public RelationQueryBuilder(RdbmsEntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public SqlQueryMeta.Factory buildMetaFactory(Object mainObj, String propertyName) {
		RdbmsEntity entity = entityManager.getEntity(ClassUtils.getUserClass(mainObj.getClass()));
		return buildMetaFactory(entity, mainObj, propertyName);
	}

	public SqlQueryMeta.Factory buildMetaFactory(RdbmsEntity entity, Object mainObj, String propertyName) {
		Class relClazz = entity.getRelMeta(propertyName).getJoinPropertyType();
		RdbmsEntity relEntity = entityManager.getEntity(relClazz);

		String sql = entity.buildRelFindSql(propertyName, relEntity);
		return SqlQueryMeta.factory().
				sql(sql).
				mappingClazz(relClazz).
				params(entity.getPkValues(mainObj));
	}

}
