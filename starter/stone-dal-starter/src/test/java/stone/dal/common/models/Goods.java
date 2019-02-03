package stone.dal.common.models;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.adaptor.spring.jdbc.aop.example.SampleColumnMapper;
import stone.dal.common.models.annotation.ColumnMapper;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.data.BaseDo;

@Entity
@Table(name = "goods", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Goods extends BaseDo {

  private Long uuid;

  private String name;

  private Date createDate;

  private String label;

  private Long labelId;

  @Transient
  @ColumnMapper(mapper = SampleColumnMapper.class, associateColumn = "labelId")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Column(name = "label_id", precision = 18, scale = 0)
  public Long getLabelId() {
    return labelId;
  }

  public void setLabelId(Long labelId) {
    this.labelId = labelId;
  }

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  @Sequence(generator = "sequence", defaultStartSeq = 1000)
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
