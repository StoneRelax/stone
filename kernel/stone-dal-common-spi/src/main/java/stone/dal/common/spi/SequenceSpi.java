package stone.dal.common.spi;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.FieldMeta;

public interface SequenceSpi {

  <T> T next(BaseDo obj, FieldMeta meta);
}
