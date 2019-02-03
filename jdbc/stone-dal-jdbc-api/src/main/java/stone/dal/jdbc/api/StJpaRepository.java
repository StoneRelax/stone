package stone.dal.jdbc.api;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import stone.dal.common.StRepository;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;

/**
 * @author fengxie
 */
@Repository
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
   * @param obj Object contains condition values
   * @return DO object
   */
  T findOne(T obj);

  /**
   * Find by specific pk value
   *
   * @param pk Pk value
   * @return Do object of which pk equals to the given one
   */
  T findByPk(K pk);

  /**
   * Delete record by specific pk value
   *
   * @param pk Pk value
   */
  void delByPk(K pk);


  /**
   * Find all do objects
   *
   * @return List of do objects
   */
  Collection<T> findAll();

  /**
   * Find a list by a give condition of T object
   *
   * @param condition Object contains condition values
   * @return List of DO object
   */
  Collection<T> findMany(T condition);

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
