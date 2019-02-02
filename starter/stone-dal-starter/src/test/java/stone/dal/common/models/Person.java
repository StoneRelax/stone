package stone.dal.common.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import stone.dal.adaptor.spring.jdbc.aop.example.SampleEntityListener;
import stone.dal.common.models.annotation.Clob;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.data.BaseDo;

/**
 * Created by on 5/9/2017.
 */
@Entity
@Table(name = "person", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@EntityListeners(SampleEntityListener.class)
public class Person extends BaseDo {
  private Long uuid;

  private String name;

  private List<MyOrder> myOrders = new ArrayList<>();

  private String description;

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  @Sequence(generator="sequence")
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Clob
  @Column(name = "descriptionuuid")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
