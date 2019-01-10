package stone.dal.rdbms.spi;

import stone.dal.rdbms.api.meta.SqlQueryMeta;

/**
 * @author fengxie
 */
public interface DalRdbmsResultHandlerSpi {

	Object buildRowObj(SqlQueryMeta queryMeta);
}
