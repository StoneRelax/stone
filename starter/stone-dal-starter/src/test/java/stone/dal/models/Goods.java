package stone.dal.models;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import stone.dal.models.annotation.Sequence;
import stone.dal.models.data.BaseDo;

@Entity
@Table(name = "goods", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Goods extends BaseDo {

  private Long uuid;

  private String name;

  private Date createDate;

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  @Sequence(generator = "seed")
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", length = 50)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "create_date")
  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Goods goods = (Goods) o;
    return Objects.equals(uuid, goods.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
