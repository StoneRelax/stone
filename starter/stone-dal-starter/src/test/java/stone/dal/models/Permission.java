package stone.dal.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.models.annotation.Nosql;
import stone.dal.models.annotation.UniqueIndex;
import stone.dal.models.annotation.UniqueIndices;
import stone.dal.models.data.BaseDo;

/**
 * Component:
 * Description:
 * User: feng.xie
 * Date: 08-Jan-2011
 */
@Entity
@Nosql
@Table(
    name = "permission",
    uniqueConstraints = { @UniqueConstraint(
        columnNames = { "permissionName" }
    ) }
)
@UniqueIndices(indices = { @UniqueIndex(name = "name", columnNames = { "permissionName" }) })
public class Permission extends BaseDo {

  private String permissionType;

  private String permissionName;

  private String description;

  private String linkId;

  private Boolean applied;

  @Column(name = "permissionType", length = 50, nullable = false)
  public String getPermissionType() {
    return permissionType;
  }

  public void setPermissionType(String permissionType) {
    this.permissionType = permissionType;
  }

  @Id
  @Column(name = "permissionName", length = 100)
  public String getPermissionName() {
    return permissionName;
  }

  public void setPermissionName(String permissionName) {
    this.permissionName = permissionName;
  }

  @Column(name = "description", length = 300)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Transient
  public Boolean getApplied() {
    return applied;
  }

  public void setApplied(Boolean applied) {
    this.applied = applied;
  }

  @Column(name = "linkId", length = 300)
  public String getLinkId() {
    return linkId;
  }

  public void setLinkId(String linkId) {
    this.linkId = linkId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getUserClass(getClass()) != getUserClass(o.getClass()))
      return false;

    Permission that = (Permission) o;

    return !(permissionName != null ? !permissionName.equals(that.permissionName) : that.permissionName != null);

  }

  @Override
  public int hashCode() {
    return permissionName != null ? permissionName.hashCode() : 0;
  }
}
