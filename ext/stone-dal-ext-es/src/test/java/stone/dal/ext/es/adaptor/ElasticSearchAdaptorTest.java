package stone.dal.ext.es.adaptor;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
    elasticSearchAdaptor.insert(tx, tx.getUuid().toString());

    tx = new BankTransaction();
    tx.setUuid(2L);
    tx.setUser("stone");
    tx.setType(1);
    tx.setAmount(200);
    tx.setScore(0);
    elasticSearchAdaptor.insert(tx, tx.getUuid().toString());

    Thread.sleep(1000);
    System.out.println("Sleep 1000ms for ES to sync");

    BankTransaction xfResult = elasticSearchAdaptor.queryById("1", BankTransaction.class);
    Assert.assertEquals("xf", xfResult.getUser());

//    BankTransaction queryStone = new BankTransaction();
//    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//    boolQueryBuilder.must(QueryBuilders.termQuery("user", "stone"));
//    List<BankTransaction> stoneResult = ElasticSearchUtil.getInstance()
//        .queryForList(queryStone, BankTransaction.class, null, boolQueryBuilder);
//    BankTransaction getStone = stoneResult.get(0);
//    Assert.assertEquals(200, getStone.getAmount());

//    BankTransaction bankTransaction3 = new BankTransaction();
//    bankTransaction3.setUuid(3L);
//    bankTransaction3.setUser("xf");
//    bankTransaction3.setType(1);
//    bankTransaction3.setAmount(300);
//    bankTransaction3.setScore(30);
//    bankTransactionRepository.create(bankTransaction3);
//
//    Thread.sleep(1000);
//    System.out.println("Sleep 1000ms for ES to sync");

    BoolQueryBuilder aggBoolQueryBuilder = QueryBuilders.boolQuery();
    aggBoolQueryBuilder.must(QueryBuilders.termQuery("user", "xf"));
    List<AbstractAggregationBuilder> aggregationBuilders = new ArrayList<>();
    SumBuilder sb = AggregationBuilders.sum("totalAmount").field("amount");
    aggregationBuilders.add(sb);
    Aggregations aggregations = elasticSearchAdaptor
        .aggregationQuery(BankTransaction.class, null, aggBoolQueryBuilder, aggregationBuilders);
    Sum sum = aggregations.get("totalAmount");
    Assert.assertEquals(400, sum.getValue(), 0.001);

//    long count = ElasticSearchUtil.getInstance().count(emptyTransaction, null, null);
//    Assert.assertEquals(3, count);
//
//    BankTransaction deleteTransaction = new BankTransaction();
//    deleteTransaction.setUuid(1L);
//    ElasticSearchUtil.getInstance().remove(deleteTransaction);
//    Thread.sleep(1000);
//    System.out.println("Sleep 1000ms for ES to sync");
//    Assert.assertEquals(0, ElasticSearchUtil.getInstance().queryForList(tx, BankTransaction.class, null, null).size());
  }
}
