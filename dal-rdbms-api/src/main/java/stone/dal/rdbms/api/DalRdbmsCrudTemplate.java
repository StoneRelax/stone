package stone.dal.rdbms.api;

import stone.dal.common.api.DalObj;
import stone.dal.rdbms.api.meta.SqlCondition;

import java.util.List;

/**
 * @author fengxie
 */
public interface DalRdbmsCrudTemplate<T extends DalObj, K> {


	/**
	 * @param obj
	 * @return
	 * @
	 */
	K create(T obj);

	/**
	 * @param obj
	 * @
	 */
	void update(T obj);

	/**
	 * @param obj
	 * @
	 */
	void del(T obj);

	/**
	 * @param pk
	 * @return
	 * @
	 */
	T get(T pk);

	/**
	 * @param obj
	 * @return
	 * @
	 */
	K create(T obj, String dsSchema);

	/**
	 * Update db record by a given dalObject
	 *
	 * @param dalObj   Dal object
	 * @param dsSchema DB schema
	 */
	void update(T dalObj, String dsSchema);

	/**
	 * Delete db record by a specific primary key
	 *
	 * @param pkObj    Object with primary key values
	 * @param dsSchema DB schema
	 */
	void del(T pkObj, String dsSchema);

	/**
	 * Find by a dal object, object with pk
	 *
	 * @param pkObj    Object with primary key values
	 * @param dsSchema DB schema
	 * @return Returns dal object
	 */
	T get(T pkObj, String dsSchema);

	/**
	 * Find one by one obj
	 *
	 * @param condition Condition object
	 * @return Returns dal object
	 */
	T findOne(SqlCondition condition);

	/**
	 * Find many objects by condition
	 *
	 * @param condition Condition object
	 * @return Returns dal objects
	 */
	List<T> findMany(SqlCondition condition);

	/**
	 * Find many objects by condition
	 *
	 * @param condition Condition object
	 * @param dsSchema  DB schema
	 * @return List
	 */
	List<T> findMany(SqlCondition condition, String dsSchema);

	/**
	 * Find one by condition object
	 *
	 * @param condition Condition object
	 * @param dsSchema  Schema
	 * @return Returns dal object
	 */
	T findOne(SqlCondition condition, String dsSchema);
}
