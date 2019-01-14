package stone.dal.jdbc.spring.adaptor.jdbc;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.spring.adaptor.init.SpringJdbcAdaptorTestApplication;
import stone.dal.models.MyOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringJdbcAdaptorTestApplication.class)
public class StJdbcTemplateTest {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @Test
  public void testQuery() {
    SqlQueryMeta queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid>? order by uuid").
        params(new Object[] { 1 }).
        mappingClazz(MyOrder.class).build();
    List<MyOrder> orderList = stJdbcTemplate.query(queryMeta);
    Assert.assertEquals(orderList.get(0).getUuid(), new Long(2));
  }

}
