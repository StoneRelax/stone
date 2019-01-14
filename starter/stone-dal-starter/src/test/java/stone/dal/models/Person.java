package stone.dal.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import stone.dal.models.data.BaseDo;

/**
 * Created by on 5/9/2017.
 */
@Entity
@Table(name = "person", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Person extends BaseDo {
  private Long uuid;

  private String name;

  private List<MyOrder> myOrders = new ArrayList<>();

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", length = 50, nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
  @JoinTable(name = "person_order", joinColumns = { @JoinColumn(name = "person_uuid", referencedColumnName = "uuid") },
      inverseJoinColumns = { @JoinColumn(name = "order_uuid", referencedColumnName = "uuid") })
  public List<MyOrder> getMyOrders() {
    return myOrders;
  }

  public void setMyOrders(List<MyOrder> myOrders) {
    this.myOrders = myOrders;
  }
}
