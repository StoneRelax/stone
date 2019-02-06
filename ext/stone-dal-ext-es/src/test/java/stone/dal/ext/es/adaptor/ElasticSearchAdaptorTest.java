package stone.dal.ext.es.adaptor;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.ext.es.app.SpringEsAdaptorTestApplication;
import stone.dal.ext.es.models.BankTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringEsAdaptorTestApplication.class)
public class ElasticSearchAdaptorTest {

  @Autowired
  private ElasticSearchAdaptor<BankTransaction> elasticSearchAdaptor;

  @Test
  public void testES() throws Exception {
    elasticSearchAdaptor.removeIndex("bank_transaction");
    BankTransaction tx = new BankTransaction();
    tx.setUuid(1L);
    tx.setUser("xf");
    tx.setType(0);
    tx.setAmount(100);
    tx.setScore(10);
    elasticSearchAdaptor.insert(tx);

    tx = new BankTransaction();
    tx.setUuid(2L);
    tx.setUser("stone");
    tx.setType(1);
    tx.setAmount(200);
    tx.setScore(0);
    elasticSearchAdaptor.insert(tx);

    Thread.sleep(1000);
    System.out.println("Sleep 1000ms for ES to sync");

    BankTransaction xfResult = elasticSearchAdaptor.queryById("1", BankTransaction.class);
    Assert.assertEquals("xf", xfResult.getUser());

    List<AbstractAggregationBuilder> aggregationBuilders = new ArrayList<>();
    SumBuilder sb = AggregationBuilders.sum("totalAmount").field("amount");
    aggregationBuilders.add(sb);
    Aggregations aggregations = elasticSearchAdaptor
        .aggregationQuery(BankTransaction.class, null, null, aggregationBuilders);
    Sum sum = aggregations.get("totalAmount");
    Assert.assertEquals(300, sum.getValue(), 0.001);

    long count = elasticSearchAdaptor.count(BankTransaction.class, null, null);
    Assert.assertEquals(2, count);
//
//    BankTransaction deleteTransaction = new BankTransaction();
//    deleteTransaction.setUuid(1L);
    elasticSearchAdaptor.remove(BankTransaction.class, "1");
    Thread.sleep(1000);
    count = elasticSearchAdaptor.count(BankTransaction.class, null, null);
    Assert.assertEquals(1, count);
//    Assert.assertEquals(0, ElasticSearchUtil.getInstance().queryForList(tx, BankTransaction.class, null, null).size());
  }
}
