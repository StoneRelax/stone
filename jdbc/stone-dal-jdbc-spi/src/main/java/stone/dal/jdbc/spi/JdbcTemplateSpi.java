package stone.dal.jdbc.spi;

import java.util.List;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.data.Page;
import stone.dal.jdbc.api.meta.SqlBaseMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;

/**
 * @author fengxie
 */
public interface JdbcTemplateSpi<T extends BaseDo> {

  int exec(SqlBaseMeta meta);

  List<T> query(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);

  Page<T> queryPage(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper);
}
