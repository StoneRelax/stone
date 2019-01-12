package stone.dal.jdbc.api;

import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface JpaRepository<T extends BaseDo, K> {

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

}
