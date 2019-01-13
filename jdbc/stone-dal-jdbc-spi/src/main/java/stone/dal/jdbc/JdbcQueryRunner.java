package stone.dal.jdbc;

import java.util.List;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

/**
 * @author fengxie
 */
public interface JdbcQueryRunner<T> {

  List<T> run(SqlQueryMeta queryMeta);

  @SuppressWarnings("unchecked")
  Page<T> runPagination(SqlQueryMeta queryMeta);

  <T extends BaseDo> T runFind(BaseDo pk);
}
