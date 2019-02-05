package stone.dal.adaptor.spring.jdbc.aop.example;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.common.models.Person;

public class SampleEntityListener {

  @PrePersist
  public void beforeCreate(Person person) {
    person.setCreatedDate(new Timestamp(new Date().getTime()));
  }

  @PostPersist
  public void afterCreate(Person person) {

  }

  @PreUpdate
  public void beforeUpdate(Person person) {
    person.setLastUpdateDate(new Timestamp(new Date().getTime()));
  }

  @PostUpdate
  public void afterUpdate(Person person) {

  }

  @PreRemove
  public void beforeRemove(Person person) {

  }

  @PostRemove
  public void afterRemove(Person person) {

  }

}
