package stone.dal.kernel.utils;

import java.awt.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by  on 12/9/2016.
 */
public class User {
  private Long id;

  private String name;

  //@JSONField(format="yyyy-MM-dd HH:mm:ss")
  private Date birthday;

  private Timestamp createTime;

  private transient Group group;

  private Color color;

  public User() {
  }

  public User(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
