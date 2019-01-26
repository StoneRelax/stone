package stone.dal.adaptor.spring.jdbc.api;

import stone.dal.common.StRepository;
import stone.dal.common.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface StJpaRepository<T extends BaseDo, K> extends StRepository {

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