package stone.dal.metadata.operator;

import stone.dal.metadata.meta.AppException;

/**
 * @author fengxie
 */
public interface Visitor {

	/**
	 * Callback interface
	 *
	 * @param element Element of array or collection
	 * @return Replace element
	 */
	Object visit(Object element) throws AppException;

}
