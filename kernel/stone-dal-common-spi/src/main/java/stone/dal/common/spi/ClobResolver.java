package stone.dal.common.spi;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;

public interface ClobResolver {

  String resolve(BaseDo obj, EntityMeta meta, String field);

}
