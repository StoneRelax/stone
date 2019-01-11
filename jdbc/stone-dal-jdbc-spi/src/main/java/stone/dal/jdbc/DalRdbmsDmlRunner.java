package stone.dal.jdbc;

import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface DalRdbmsDmlRunner<T extends BaseDo> {

  int run(SqlDmlDclMeta meta);

  void runInsert(T obj);

  void runDelete(T obj);

  void runUpdate(T obj);
}
