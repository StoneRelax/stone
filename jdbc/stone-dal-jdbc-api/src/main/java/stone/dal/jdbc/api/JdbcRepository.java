package stone.dal.jdbc.api;

import java.util.List;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface JdbcRepository<T extends BaseDo, K> {

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

//	/**
//	 * Find one by one obj
//	 *
//	 * @param condition Condition object
//	 * @return Returns dal object
//	 */
//	T findOne(SqlCondition condition);

  /**
   * Find many objects by condition
   *
   * @param condition Condition object
   * @return Returns dal objects
   */
  List<T> findMany(SqlCondition condition);
}
