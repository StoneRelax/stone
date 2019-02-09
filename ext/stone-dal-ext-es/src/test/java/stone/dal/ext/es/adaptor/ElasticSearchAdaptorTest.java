package stone.dal.ext.es.adaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.web.PageableDefault;
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
    Pageable pageable = new PageRequest(0,10);
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
        .aggregationQuery(BankTransaction.class, null, null, null,null,null,aggregationBuilders);
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

    QueryBuilder aggFilter1 = QueryBuilders.rangeQuery("amount").from(-10000).to(10000);
    QueryBuilder aggFilter2 = QueryBuilders.rangeQuery("score").from(-10000).to(10000);

    AggregationBuilder mainFilter = AggregationBuilders.filter("totalAmount").filter(aggFilter1);
    AggregationBuilder scoreFilter = AggregationBuilders.filter("totalPoint").filter(aggFilter2);
    mainFilter.subAggregation(scoreFilter);
    TermsBuilder nameAggBuilder = AggregationBuilders.terms("txUser").field("user");
    aggregationBuilders = new ArrayList<>();
    SumBuilder amountBuilder = AggregationBuilders.sum("amountSum").field("amount");
    SumBuilder scoreBuilder = AggregationBuilders.sum("scoreSum").field("score");
    nameAggBuilder.subAggregation(amountBuilder);
    nameAggBuilder.subAggregation(scoreBuilder);
    scoreFilter.subAggregation(nameAggBuilder);
    aggregationBuilders.add(mainFilter);
    aggregations = elasticSearchAdaptor.aggregationQuery(BankTransaction.class,null,null,null,null,pageable,aggregationBuilders);
    if(aggregations != null){
      InternalFilter amountFilter = aggregations.get("totalAmount");
      if(amountFilter != null){
        InternalFilter pointFilter =  amountFilter.getAggregations().get("totalPoint");
        int fitUserCount = (int)pointFilter.getDocCount();
        StringTerms userIdAgg = pointFilter.getAggregations().get("txUser");
        if(userIdAgg != null){
          for(Terms.Bucket bucket: userIdAgg.getBuckets()){
            String userUid = bucket.getKeyAsString();
            long userTxCount = bucket.getDocCount();
            Sum amountSum = bucket.getAggregations().get("amountSum");
            double totalAmount = amountSum.getValue();
            Sum pointSum = bucket.getAggregations().get("scoreSum");
            long totalPoint = Math.round(pointSum.getValue());
            TxAggregationRecord record = new TxAggregationRecord(userUid,userTxCount,"",0,totalAmount,totalPoint);
            aggResults.add(record);
          }
        }
      }
    }
    Assert.assertEquals(2,aggResults.size());
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
    Page<BankTransaction> page = elasticSearchAdaptor.queryForPage(BankTransaction.class,null,boolQueryBuilder,sortBuilder,rangeQueryBuilder,pageable);

    Assert.assertEquals(filterResult.size(),page.getTotalElements());
  }
}
