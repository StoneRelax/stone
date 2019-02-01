package stone.dal.adaptor.spring.jdbc.spring.adaptor.jdbc;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlCondition;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.adaptor.spring.jdbc.spring.adaptor.app.SpringJdbcAdaptorTestApplication;
import stone.dal.common.models.MyOrder;
import stone.dal.common.models.MyOrderItem;
import stone.dal.common.models.data.Page;

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

    SqlCondition condition = SqlCondition.create(MyOrder.class).gt("uuid", 1);
    orderList = stJdbcTemplate.query(condition);
    Assert.assertEquals(orderList.get(0).getUuid(), new Long(2));
    Assert.assertEquals(orderList.get(1).getUuid(), new Long(3));
  }

  @Test
  public void testPagination_noData() {
    SqlQueryMeta queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid>? order by uuid").
        params(new Object[] { 100000 }).
        pageNo(1).pageSize(1).
        mappingClazz(MyOrder.class).build();
    Page<MyOrder> page = stJdbcTemplate.pageQuery(queryMeta);
    Assert.assertEquals(1, page.getPageInfo().getPageNo());
    Assert.assertEquals(0, page.getPageInfo().getTotalRows());
    Assert.assertEquals(0, page.getRows().size());
  }

  @Test
  public void testPagination() {
    SqlQueryMeta queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid>? order by uuid").
        params(new Object[] { 1 }).
        pageNo(1).pageSize(1).
        mappingClazz(MyOrder.class).build();
    Page<MyOrder> page = stJdbcTemplate.pageQuery(queryMeta);
    Assert.assertEquals(1, page.getPageInfo().getPageNo());
    Assert.assertEquals(2, page.getPageInfo().getTotalRows());
    Assert.assertEquals(1, page.getRows().size());
    Assert.assertEquals("M00002", page.getRows().get(0).getOrderNo());

    queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid>? order by uuid").
        params(new Object[] { 1 }).
        pageNo(1).pageSize(2).
        mappingClazz(MyOrder.class).build();
    page = stJdbcTemplate.pageQuery(queryMeta);
    Assert.assertEquals(1, page.getPageInfo().getPageNo());
    Assert.assertEquals(2, page.getPageInfo().getTotalRows());
    Assert.assertEquals(2, page.getRows().size());

    queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid>? order by uuid").
        params(new Object[] { 1 }).
        pageNo(2).pageSize(1).
        mappingClazz(MyOrder.class).build();
    page = stJdbcTemplate.pageQuery(queryMeta);
    Assert.assertEquals(2, page.getPageInfo().getPageNo());
    Assert.assertEquals(2, page.getPageInfo().getTotalRows());
    Assert.assertEquals(1, page.getRows().size());
    Assert.assertEquals("M00003", page.getRows().get(0).getOrderNo());
  }

  @Test
  public void testLazyQuery() {
    SqlQueryMeta queryMeta = SqlQueryMeta.factory().
        sql("select * from my_order where uuid=?").
        params(new Object[] { 1 }).
        supportFetchMore(true).
        mappingClazz(MyOrder.class).build();
    MyOrder order = stJdbcTemplate.queryOne(queryMeta);
    List<MyOrderItem> items = order.getItems();
    Assert.assertTrue(!items.isEmpty());
    Assert.assertEquals("ORDER1", items.stream().filter(item -> item.getUuid() == 1L).findFirst().get().getItemName());
  }

}
