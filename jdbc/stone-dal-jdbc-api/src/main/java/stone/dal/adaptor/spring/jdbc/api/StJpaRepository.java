package stone.dal.adaptor.spring.jdbc.api;

import java.util.Collection;
import stone.dal.common.StRepository;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;

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

  Collection<T> findList(T condition);

  /**
   * Query with a given page number
   *
   * @param obj      Query object
   * @param pageSize Page Size
   * @param pageNo   Page No
   * @return Page object
   */
  Page<T> pageQuery(T obj, int pageSize, int pageNo);

}
