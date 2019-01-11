package stone.dal.jdbc;

import stone.dal.jdbc.api.meta.SqlQueryMeta;

/**
 * @author fengxie
 */
public interface JdbcResultHandlerSpi {

	Object buildRowObj(SqlQueryMeta queryMeta);
}
