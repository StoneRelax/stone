package stone.dal.common.spi;

import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.FieldMeta;

public interface SequenceSpi {

  <T> T next(BaseDo obj, FieldMeta meta);
}
