package stone.dal.models.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengxie
 */
public class Page<T> {

	/**
	 * Page Number
	 */
	private int total;
	/**
	 * Current Page Number
	 */
	private int pageNo;
	/**
	 * Total count
	 */
	private int totalCount;
	/**
	 * Record set of Current Page
	 */
	private List<T> rows = new ArrayList<>();

	/**
	 * Load Page Numbers
	 *
	 * @return Number of Pages
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Set Page Numbers
	 *
	 * @param total Number of Pages
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Load Current Page Number
	 *
	 * @return Current Page Number
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * Set Current Page Number
	 *
	 * @param pageNo Current Page Number
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * Load Page Recordset Info
	 *
	 * @return Recordsets in Current Page
	 */
	public List<T> getRows() {
		return rows;
	}

	/**
	 * Set Page Recordset Info
	 *
	 * @param rows Recordsets in Current Page
	 */
	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
