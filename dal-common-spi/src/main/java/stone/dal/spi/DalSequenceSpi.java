package stone.dal.spi;

import stone.dal.common.api.DalObj;

/**
 * @author fengxie
 */
public interface DalSequenceSpi {

	<T> T next(DalObj obj, String field);
}
