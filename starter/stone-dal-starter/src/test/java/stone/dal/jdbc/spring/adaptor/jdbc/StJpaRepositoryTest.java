package stone.dal.jdbc.spring.adaptor.jdbc;

import java.sql.Timestamp;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.spring.adaptor.app.SpringJdbcAdaptorTestApplication;
import stone.dal.models.Goods;
import stone.dal.models.MyOrder;
import stone.dal.models.MyOrderItem;
import stone.dal.models.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringJdbcAdaptorTestApplication.class)
public class StJpaRepositoryTest {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @Autowired
  private StJpaRepository<MyOrder, Long> myOrderRepository;

  @Autowired
  private StJpaRepository<Person, Long> personRepository;

  @Autowired
  private StJpaRepository<Goods, Long> goodsRepository;

  @Before
  public void before() {
    SqlDmlDclMeta baseMeta = SqlDmlDclMeta.factory()
        .sql("delete from my_order where uuid>?")
        .params(new Object[] { 100L }).build();
    stJdbcTemplate.execDml(baseMeta);

    baseMeta = SqlDmlDclMeta.factory()
        .sql("delete from my_order_item where uuid>?")
        .params(new Object[] { 100L }).build();
    stJdbcTemplate.execDml(baseMeta);

    baseMeta = SqlDmlDclMeta.factory()
        .sql("delete from person_order where person_uuid>? and order_uuid>?")
        .params(new Object[] { 100L, 100L }).build();
    stJdbcTemplate.execDml(baseMeta);

    baseMeta = SqlDmlDclMeta.factory()
        .sql("delete from person where uuid>?")
        .params(new Object[] { 100L }).build();
    stJdbcTemplate.execDml(baseMeta);
  }

  @Test
  public void testCreate() {
    MyOrder order = buildTestingOrder();

    Assert.assertEquals(new Long(1000L), myOrderRepository.create(order));

    MyOrder pk = new MyOrder();
    pk.setUuid(1000L);
    MyOrder _order = myOrderRepository.get(pk);
    Assert.assertEquals(new Long(1000), _order.getUuid());
    Assert.assertEquals("M1000L", _order.getOrderDesc());

    Assert.assertEquals(2, _order.getItems().size());
    Assert.assertEquals("ITEM0001", _order.getItems().get(0).getItemName());
    Assert.assertEquals("ITEM0002", _order.getItems().get(1).getItemName());

    myOrderRepository.del(pk);

    _order = myOrderRepository.get(pk);
    Assert.assertNull(_order);
    List res = stJdbcTemplate.query(SqlQueryMeta.factory().sql("select * from my_order_item where uuid>=?").params(
        new Object[] { 10000L }).build());
    Assert.assertEquals(0, res.size());
  }

  @Test
  public void testCreate_withSeq() {
    Goods goods = new Goods();
    goods.setName("plan_model");

    Long uuid = goodsRepository.create(goods);
    Assert.assertEquals(new Long(1001), uuid);

//    Per _person = personRepository.get(goods);
//    Assert.assertEquals(new Long(1001l), _person.getUuid());
//    Assert.assertEquals("jinny", _person.getName());
//    Assert.assertEquals("M00001", _person.getMyOrders().get(0).getOrderNo());
//    Assert.assertEquals(new Long(1), _person.getMyOrders().get(0).getUuid());
//    Assert.assertEquals("M00002", _person.getMyOrders().get(1).getOrderNo());
//    Assert.assertEquals(new Long(2), _person.getMyOrders().get(1).getUuid());
  }


  @Test
  public void testCreate_many2many() {
    Person person = new Person();
    person.setUuid(1001l);
    person.setName("jinny");

    MyOrder order = new MyOrder();
    order.setUuid(1L);

    MyOrder order2 = new MyOrder();
    order2.setUuid(2L);

    person.getMyOrders().add(order);
    person.getMyOrders().add(order2);

    Assert.assertEquals(new Long(1001), personRepository.create(person));

    Person _person = personRepository.get(person);
    Assert.assertEquals(new Long(1001l), _person.getUuid());
    Assert.assertEquals("jinny", _person.getName());
    Assert.assertEquals("M00001", _person.getMyOrders().get(0).getOrderNo());
    Assert.assertEquals(new Long(1), _person.getMyOrders().get(0).getUuid());
    Assert.assertEquals("M00002", _person.getMyOrders().get(1).getOrderNo());
    Assert.assertEquals(new Long(2), _person.getMyOrders().get(1).getUuid());
  }

  @Test
  public void testRemove_many2many() {
    testCreate_many2many();
    Person person = new Person();
    person.setUuid(1001L);
    personRepository.del(person);
    Assert.assertNull(personRepository.get(person));
    SqlQueryMeta queryMeta = SqlQueryMeta.factory()
        .sql("select * from person_order where person_uuid=?")
        .params(new Object[] { 1001L }).build();
    List res = stJdbcTemplate.query(queryMeta);
    Assert.assertTrue(res.isEmpty());
  }

  private MyOrder buildTestingOrder() {
    MyOrder order = new MyOrder();
    order.setUuid(1000L);
    order.setOrderDesc("M1000L");
    order.setOrderNo("M1000_NO");
    order.setCreateDate(new Timestamp(System.currentTimeMillis()));

    MyOrderItem orderItem = new MyOrderItem();
    orderItem.setUuid(10000L);
    orderItem.setItemName("ITEM0001");
    order.getItems().add(orderItem);

    orderItem = new MyOrderItem();
    orderItem.setUuid(10001L);
    orderItem.setItemName("ITEM0002");
    order.getItems().add(orderItem);

    return order;
  }
}
