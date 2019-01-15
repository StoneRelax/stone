package stone.dal.common.spi;

import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.EntityMeta;

public interface ClobResolver {

  String resolve(BaseDo obj, EntityMeta meta, String field);

}
