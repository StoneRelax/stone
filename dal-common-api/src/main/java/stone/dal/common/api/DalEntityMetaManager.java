package stone.dal.common.api;

import stone.dal.common.api.meta.EntityMeta;

/**
 * @author fengxie
 */
public interface DalEntityMetaManager {

	EntityMeta getEntity(Class clazz);

	EntityMeta getEntityByClazzName(String name);

}
