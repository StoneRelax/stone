package stone.dal.common.spi;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;

public interface ClobResolverSpi {

  String create(BaseDo obj, EntityMeta meta, String field);

  void delete(BaseDo obj, EntityMeta meta, String field);

  String read(BaseDo obj,EntityMeta meta,String field);

}
