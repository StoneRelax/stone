package stone.dal.jdbc.spring.adaptor.models;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.models.annotation.Sequence;
import stone.dal.models.data.BaseDo;

/**
 * Component Name:
 * Description:
 *
 * @author feng.xie
 * @version $Revision: $
 */
@Entity
@Table(name = "drone_role", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Role extends BaseDo {

  private Long uuid;

  private String roleName;

  private String createBy;

  private String updateBy;

  private Timestamp createDate;

  private Timestamp updateDate;

  private List<Permission> permissionList;

  private List<User> users;

  private Boolean builtin;

  @Column(name = "builtin")
  public Boolean getBuiltin() {
    return builtin;
  }

  public void setBuiltin(Boolean builtin) {
    this.builtin = builtin;
  }

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  @Sequence(key = "Role.uuid", generator = "number")
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  @Column(name = "role_name", length = 32)
  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  @Column(name = "create_by", length = 32)
  @Sequence(generator = "default")
  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  @Column(name = "update_by", length = 32)
  @Sequence(generator = "default")
  public String getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(String updateBy) {
    this.updateBy = updateBy;
  }

  @Column(name = "create_date")
  @Sequence(generator = "default")
  public Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
  }

  @Column(name = "update_date")
  @Sequence(generator = "default")
  public Timestamp getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Timestamp updateDate) {
    this.updateDate = updateDate;
  }

  @Transient
  public List<Permission> getPermissionList() {
    return permissionList;
  }

  public void setPermissionList(List<Permission> permissionList) {
    this.permissionList = permissionList;
  }

  @Transient
  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getUserClass(getClass()) != getUserClass(o.getClass()))
      return false;

    Role role = (Role) o;

    return !(uuid != null ? !uuid.equals(role.uuid) : role.uuid != null);

  }

  @Override
  public int hashCode() {
    return uuid != null ? uuid.hashCode() : 0;
  }
}
