package stone.dal.adaptor.es.example;

import stone.dal.common.models.data.BaseDo;
import stone.dal.spring.es.lib.ElasticSearchUtil;
import stone.dal.common.models.Person;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.sql.Timestamp;
import java.util.Date;

public class SampleEntityListener {

   private static final ElasticSearchUtil esUtil = ElasticSearchUtil.getInstance();


  @PrePersist
  public void beforeCreate(BaseDo obj) {
    esUtil.add(obj);
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
