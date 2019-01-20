package stone.dal.common.models;

import java.sql.Timestamp;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.common.models.annotation.FieldMapper;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.data.BaseDo;

@Entity
@Table(name = "drone_user", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class User extends BaseDo {

  private Long uuid;

  private String createBy;

  private String updateBy;

  private Timestamp createDate;

  private Timestamp updateDate;

  private List<Role> roles;

  protected String userId;

  protected String userName;

  protected String password;

  private Boolean manager;

  private String hlevel;

  private String parentName;

  private String parentType;

  private Long parentUuid;

  private Integer retryTimes;

  private Integer loginTimes;

  private Integer sno;

  private Boolean supervisor;

  private String locale;

  private Integer age;

  private String gender;

  private Long genderUuid;

  private Date birthday;

  private Boolean builtin;

  @Column(name = "builtin")
  public Boolean getBuiltin() {
    return builtin;
  }

  public void setBuiltin(Boolean builtin) {
    this.builtin = builtin;
  }

  @Transient
  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  @Transient
  public Boolean getSupervisor() {
    return supervisor;
  }

  public void setSupervisor(Boolean supervisor) {
    this.supervisor = supervisor;
  }

  @Column(name = "retry_times", precision = 5, scale = 0)
  public Integer getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(Integer retryTimes) {
    this.retryTimes = retryTimes;
  }

  @Column(name = "login_times", precision = 5, scale = 0)
  public Integer getLoginTimes() {
    return loginTimes;
  }

  public void setLoginTimes(Integer loginTimes) {
    this.loginTimes = loginTimes;
  }

  @Column(name = "sno", precision = 5, scale = 0)
  public Integer getSno() {
    return sno;
  }

  public void setSno(Integer sno) {
    this.sno = sno;
  }

  @Transient
  public String getParentType() {
    return parentType;
  }

  public void setParentType(String parentType) {
    this.parentType = parentType;
  }

  @Transient
  @FieldMapper(mapper = "organisation", mappedBy = "parentUuid")
  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  @Transient
  public String getHlevel() {
    return hlevel;
  }

  public void setHlevel(String hlevel) {
    this.hlevel = hlevel;
  }

  //	@DataAccess(name = "bean:parentUuid")
  @Column(name = "parent_uuid", precision = 18, scale = 0)
  public Long getParentUuid() {
    return parentUuid;
  }

  public void setParentUuid(Long parentUuid) {
    this.parentUuid = parentUuid;
  }

  @Column(name = "user_id", length = 50, nullable = false)
  public String getUserId() {
    return userId;
  }

  @Id
  @Column(name = "uuid", precision = 18, scale = 0)
  public Long getUuid() {
    return uuid;
  }

  public void setUuid(Long uuid) {
    this.uuid = uuid;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Column(name = "user_name", length = 32, nullable = false)
  public String getUserName() {
    return userName;
  }

  @Column(name = "password", length = 32, nullable = false)
  public String getPassword() {
    return password;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "manager", length = 50)
  public Boolean getManager() {
    return manager;
  }

  public void setManager(Boolean manager) {
    this.manager = manager;
  }

  @Column(name = "create_by", length = 50)
  @Sequence(generator = "userCreate")
  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  @Column(name = "update_by", length = 50)
  @Sequence(generator = "userUpdate", overrideAllowed = true)
  public String getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(String updateBy) {
    this.updateBy = updateBy;
  }

  @Column(name = "create_date")
  @Sequence(generator = "createDate")
  public Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
  }

  @Column(name = "update_date")
  @Sequence(generator = "updateDate", overrideAllowed = true)
  public Timestamp getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Timestamp updateDate) {
    this.updateDate = updateDate;
  }

  @ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
  @JoinTable(name = "drone_user_role", joinColumns = { @JoinColumn(name = "user_uuid", referencedColumnName = "uuid") },
      inverseJoinColumns = { @JoinColumn(name = "role_uuid", referencedColumnName = "uuid") })
  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  @Transient
  @FieldMapper(
      mapper = "todayOffset",
      mappedBy = "birthday"
  )
  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Transient
  @FieldMapper(
      mapper = "code",
      mappedBy = "genderUuid"
  )
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  @Column(name = "gender_uuid", precision = 18, scale = 0)
  public Long getGenderUuid() {
    return genderUuid;
  }

  public void setGenderUuid(Long genderUuid) {
    this.genderUuid = genderUuid;
  }

  @Column(name = "birthday")
  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getUserClass(getClass()) != getUserClass(o.getClass()))
      return false;

    User user = (User) o;

    if (getUuid() != null ? !getUuid().equals(user.getUuid()) : user.getUuid() != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return getUuid() != null ? getUuid().hashCode() : 0;
  }

  @Override
  public String toString() {
    return "[uuid:" + (uuid != null ? uuid : "") + "]";
  }
}