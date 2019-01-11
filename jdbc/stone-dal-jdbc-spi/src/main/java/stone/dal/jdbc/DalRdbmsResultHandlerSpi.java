package stone.dal.jdbc;

import stone.dal.jdbc.api.meta.SqlQueryMeta;

/**
 * @author fengxie
 */
public interface DalRdbmsResultHandlerSpi {

	Object buildRowObj(SqlQueryMeta queryMeta);
}
