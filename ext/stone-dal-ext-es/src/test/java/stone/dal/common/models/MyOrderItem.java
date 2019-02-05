package stone.dal.common.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import stone.dal.common.models.data.BaseDo;

/**
 * @author fengxie
 */
@Entity
@Table(name = "my_order_item", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class MyOrderItem extends BaseDo {
  private Long uuid;

  private String itemName;

  private MyOrder myOrder;

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Column(name = "item_name", length = 50)
  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  @ManyToOne(
      cascade = { CascadeType.REFRESH },
      fetch = FetchType.LAZY
  )
  @JoinColumn(
      name = "order_uuid",
      referencedColumnName = "uuid",
      nullable = false)
  public MyOrder getMyOrder() {
    return myOrder;
  }

  public void setMyOrder(MyOrder myOrder) {
    this.myOrder = myOrder;
  }
}
