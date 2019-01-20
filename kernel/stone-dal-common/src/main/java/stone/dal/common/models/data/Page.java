package stone.dal.common.models.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengxie
 */
public class Page<T> {

  private PageInfo pageInfo;
	/**
	 * Record set of Current Page
	 */
	private List<T> rows = new ArrayList<>();

  public Page(PageInfo pageInfo, List<T> rows) {
    this.pageInfo = pageInfo;
    this.rows = rows;
  }

  public PageInfo getPageInfo() {
    return pageInfo;
	}

	/**
	 * Load Page Recordset Info
	 *
	 * @return Recordsets in Current Page
	 */
	public List<T> getRows() {
		return rows;
	}

  public static PageInfo createInfo(int pageNo, int total, int totalRows) {
    return new PageInfo(pageNo, total, totalRows);
  }

  public static class PageInfo {
    private int pageNo;

    private int total;

    private int totalRows;

    public PageInfo(int pageNo, int total, int totalRows) {
      this.pageNo = pageNo;
      this.total = total;
      this.totalRows = totalRows;
    }

    public int getTotalRows() {
      return totalRows;
    }

    public int getPageNo() {
      return pageNo;
    }

    public int getTotal() {
      return total;
    }
	}
}
