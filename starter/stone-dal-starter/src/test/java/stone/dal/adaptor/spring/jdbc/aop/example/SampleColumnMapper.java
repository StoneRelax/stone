package stone.dal.adaptor.spring.jdbc.aop.example;

import stone.dal.common.models.DalColumnMapper;
import stone.dal.common.models.Goods;

public class SampleColumnMapper implements DalColumnMapper<Goods, String> {

  @Override
  public String map(Goods rowObj, String column, String associateColumn, String args) {
    return "Mapped";
  }
}
