package stone.dal.jdbc;

import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

import java.util.List;

/**
 * @author fengxie
 */
public interface JdbcQueryRunner<T> {

  List<T> run(SqlQueryMeta queryMeta);

  @SuppressWarnings("unchecked")
  Page<T> runPagination(SqlQueryMeta queryMeta);

  <T extends BaseDo> T runFind(BaseDo pk);
}
