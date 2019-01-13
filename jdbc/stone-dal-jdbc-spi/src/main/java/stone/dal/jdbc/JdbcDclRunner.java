package stone.dal.jdbc;

import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface JdbcDclRunner {

  int run(String sql);

}
