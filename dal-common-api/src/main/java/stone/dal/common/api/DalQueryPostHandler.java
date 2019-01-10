package stone.dal.common.api;


import stone.dal.kernel.AppException;

import java.util.Collection;

/**
 * @author fengxie
 */
public interface DalQueryPostHandler<T> {
	/**
	 * Handle logic after loaded all rows
	 *
	 * @param rows    <code>java.util.Collection</code>
	 */
	void readRows(Collection<T> rows) throws AppException;

	/**
	 * Handle logic after loaded row object
	 *
	 * @param rowObj  Row object
	 * @return If false, stop iteration
	 */
	boolean readRow(T rowObj) throws AppException;
}
