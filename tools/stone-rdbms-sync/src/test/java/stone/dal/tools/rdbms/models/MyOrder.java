package stone.dal.tools.rdbms.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import stone.dal.common.models.data.BaseDo;

/**
 * @author fengxie
 */
@Entity
@Table(name = "my_order", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class MyOrder extends BaseDo {
  private Long uuid;

  private String orderNo;

  private BigDecimal chargeAmt;

  private String orderDesc;

  private Timestamp createDate;

  private List<MyOrderItem> items = new ArrayList<>();

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Column(name = "order_no", length = 50, nullable = false)
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  @Column(name = "create_date")
  public Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
  }

  @Column(name = "charge_amt", precision = 9, scale = 2)
  public BigDecimal getChargeAmt() {
    return chargeAmt;
  }

  public void setChargeAmt(BigDecimal chargeAmt) {
    this.chargeAmt = chargeAmt;
  }

  @Column(name = "order_desc", length = 255)
  public String getOrderDesc() {
    return orderDesc;
  }

  public void setOrderDesc(String orderDesc) {
    this.orderDesc = orderDesc;
  }

  @OneToMany(
      cascade = { CascadeType.ALL },
      mappedBy = "myOrder"
  )
  public List<MyOrderItem> getItems() {
    return items;
  }

  public void setItems(List<MyOrderItem> items) {
    this.items = items;
  }

}
