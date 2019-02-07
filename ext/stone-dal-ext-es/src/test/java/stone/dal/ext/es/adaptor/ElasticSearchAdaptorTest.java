package stone.dal.ext.es.adaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.ext.es.app.SpringEsAdaptorTestApplication;
import stone.dal.ext.es.models.BankTransaction;
import stone.dal.ext.es.models.TxAggregationRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringEsAdaptorTestApplication.class)
public class ElasticSearchAdaptorTest {

  @Autowired
  private ElasticSearchAdaptor<BankTransaction> elasticSearchAdaptor;

  @Test
  public void testES() throws Exception {
    elasticSearchAdaptor.removeIndex("bank_transaction");
    Date current = new Date();
    BankTransaction tx = new BankTransaction();
    tx.setUuid(1L);
    tx.setUser("xf");
    tx.setType(0);
    tx.setAmount(100);
    tx.setScore(10);
    tx.setCreationDate(current);
    elasticSearchAdaptor.insert(tx);

    tx = new BankTransaction();
    tx.setUuid(2L);
    tx.setUser("stone");
    tx.setType(1);
    tx.setAmount(200);
    tx.setScore(0);
    tx.setCreationDate(current);
    elasticSearchAdaptor.insert(tx);

    tx = new BankTransaction();
    tx.setUuid(3L);
    tx.setUser("stone");
    tx.setType(0);
    tx.setAmount(-100);
    tx.setScore(100);
    tx.setCreationDate(current);
    elasticSearchAdaptor.insert(tx);

    tx = new BankTransaction();
    tx.setUuid(4L);
    tx.setUser("stone");
    tx.setType(1);
    tx.setAmount(300);
    tx.setScore(100);
    tx.setCreationDate(current);
    elasticSearchAdaptor.insert(tx);

    Thread.sleep(1000);

    Date currentLater = new Date();
    tx = new BankTransaction();
    tx.setUuid(5L);
    tx.setUser("xf");
    tx.setType(1);
    tx.setAmount(300);
    tx.setScore(100);
    tx.setCreationDate(currentLater);
    elasticSearchAdaptor.insert(tx);

    tx = new BankTransaction();
    tx.setUuid(6L);
    tx.setUser("xf");
    tx.setType(0);
    tx.setAmount(400);
    tx.setScore(50);
    tx.setCreationDate(currentLater);
    elasticSearchAdaptor.insert(tx);

    Thread.sleep(1000);
    System.out.println("Sleep 1000ms for ES to sync");

    BankTransaction xfResult = elasticSearchAdaptor.queryById("1", BankTransaction.class);
    Assert.assertEquals("xf", xfResult.getUser());
    Assert.assertEquals(current,xfResult.getCreationDate());

    List<AbstractAggregationBuilder> aggregationBuilders = new ArrayList<>();
    SumBuilder sb = AggregationBuilders.sum("totalAmount").field("amount");
    aggregationBuilders.add(sb);
    Aggregations aggregations = elasticSearchAdaptor
        .aggregationQuery(BankTransaction.class, null, null, null,null,aggregationBuilders);
    Sum sum = aggregations.get("totalAmount");
    Assert.assertEquals(1200, sum.getValue(), 0.001);

    long count = elasticSearchAdaptor.count(BankTransaction.class, null, null);
    Assert.assertEquals(6, count);

    elasticSearchAdaptor.remove(BankTransaction.class, "1");
    Thread.sleep(1000);
    count = elasticSearchAdaptor.count(BankTransaction.class, null, null);
    Assert.assertEquals(5, count);
    //report
    List<TxAggregationRecord> aggResults = new ArrayList<>();
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    TermsBuilder typeAggBuilder = AggregationBuilders.terms("txType").field("type");
    TermsBuilder nameAggBuilder = AggregationBuilders.terms("txUser").field("user");
    aggregationBuilders = new ArrayList<>();
    SumBuilder amountBuilder = AggregationBuilders.sum("totalAmount").field("amount");
    SumBuilder scoreBuilder = AggregationBuilders.sum("totalScore").field("score");
    typeAggBuilder.subAggregation(amountBuilder);
    typeAggBuilder.subAggregation(scoreBuilder);
    nameAggBuilder.subAggregation(typeAggBuilder);
    aggregationBuilders.add(nameAggBuilder);
    aggregations = elasticSearchAdaptor.aggregationQuery(BankTransaction.class,null,queryBuilder,null,null,aggregationBuilders);
    Terms terms = aggregations.get("txUser");
    if(terms.getBuckets().size() > 0){
      for(Terms.Bucket bucket : terms.getBuckets()){
        String userName = bucket.getKeyAsString();
        long userTotalCount = bucket.getDocCount();
        Terms userAgg = bucket.getAggregations().get("txType");
        for(Terms.Bucket bk : userAgg.getBuckets()){
          String typeName = bk.getKeyAsString();
          long typeCount = bk.getDocCount();
          Sum amountSum = bk.getAggregations().get("totalAmount");
          Sum scoreSum = bk.getAggregations().get("totalScore");
          TxAggregationRecord txAggregationRecord = new TxAggregationRecord(userName,userTotalCount,typeName,typeCount,amountSum.getValue(),scoreSum.getValue());
          aggResults.add(txAggregationRecord);
        }
      }
    }
    Assert.assertEquals(4,aggResults.size());
    /*
    from , to , sort by createDate
     */
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//    boolQueryBuilder.must(QueryBuilders.rangeQuery("creationDate").lte(1551801599000L).gte(0));
    SortBuilder sortBuilder = SortBuilders.fieldSort("creationDate");
    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("creationDate");
    rangeQueryBuilder.from(currentLater.getTime());
    rangeQueryBuilder.to(currentLater.getTime());
    List<BankTransaction> filterResult = elasticSearchAdaptor.queryForList(BankTransaction.class,null,boolQueryBuilder,sortBuilder,rangeQueryBuilder);
    Assert.assertEquals(2,filterResult.size());
  }
}
