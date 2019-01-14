package stone.dal.jdbc.spi;

import java.util.List;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

/**
 * @author fengxie
 */
public interface JdbcTemplateSpi<T extends BaseDo> {

  int exec(SqlDmlDclMeta meta);

  List<T> query(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);

  Page<T> runPagination(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);
}