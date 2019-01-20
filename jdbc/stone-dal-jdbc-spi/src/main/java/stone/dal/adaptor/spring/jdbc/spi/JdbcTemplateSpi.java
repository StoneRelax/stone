package stone.dal.adaptor.spring.jdbc.spi;

import java.util.List;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;

/**
 * @author fengxie
 */
public interface JdbcTemplateSpi<T extends BaseDo> {

  int exec(SqlDmlDclMeta meta);

  List<T> query(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);

  Page<T> queryPage(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);
}
