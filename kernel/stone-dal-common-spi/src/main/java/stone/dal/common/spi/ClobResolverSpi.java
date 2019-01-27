package stone.dal.common.spi;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;

public interface ClobResolverSpi {

  void create(BaseDo obj, EntityMeta meta, String clobField);

  void delete(BaseDo obj, EntityMeta meta, String clobField);

  String read(BaseDo obj, EntityMeta meta, String clobField);

}
