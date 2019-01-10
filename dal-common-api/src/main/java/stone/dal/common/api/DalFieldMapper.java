package stone.dal.common.api;

import stone.dal.kernel.AppException;
import stone.dal.kernel.KernelRuntimeException;

/**
 * @author fengxie
 */
public interface DalFieldMapper {
	<T> T getMapperVal(Object rowObj, String fieldName) throws KernelRuntimeException, AppException;
}
