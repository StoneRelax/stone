package stone.dal.adaptor.spring.jdbc.aop.example;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import stone.dal.common.models.Person;

public class SampleEntityListener {

  @PrePersist
  public void beforeCreate(Person person) {

  }

  @PostPersist
  public void afterCreate(Person person) {

  }

  @PreUpdate
  public void beforeUpdate(Person person) {

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
