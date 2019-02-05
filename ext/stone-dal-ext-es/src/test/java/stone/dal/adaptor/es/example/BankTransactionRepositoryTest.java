package stone.dal.adaptor.es.example;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import stone.dal.adaptor.es.app.SpringEsAdaptorTestApplication;
import stone.dal.adaptor.es.example.repo.BankTransactionRepository;
import stone.dal.common.models.BankTransaction;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.spring.es.lib.ElasticSearchUtil;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringEsAdaptorTestApplication.class)
public class BankTransactionRepositoryTest {

  @Autowired
  private BankTransactionRepository bankTransactionRepository;

  @Autowired
  private StJdbcTemplate jdbcTemplate;

  @Before
  public void setup() {
    jdbcTemplate.exec("delete from bank_transaction");
  }


  @Test
  public void testES() {
  
    BankTransaction bankTransaction1 = new BankTransaction();
    bankTransaction1.setUuid(1L);
    bankTransaction1.setUser("xf");
    bankTransaction1.setType(0);
    bankTransaction1.setAmount(100);
    bankTransaction1.setScore(10);
    bankTransactionRepository.create(bankTransaction1);

    BankTransaction bankTransaction2 = new BankTransaction();
    bankTransaction2.setUuid(2L);
    bankTransaction2.setUser("stone");
    bankTransaction2.setType(1);
    bankTransaction2.setAmount(200);
    bankTransaction2.setScore(0);
    bankTransactionRepository.create(bankTransaction2);

    BankTransaction queryXF = new BankTransaction();
    queryXF.setUuid(1L);
    List<BankTransaction> xfResult = ElasticSearchUtil.getInstance().query(queryXF,BankTransaction.class,null,null);
    BankTransaction getXF = xfResult.get(0);
    Assert.assertEquals("xf",getXF.getUser());

    BankTransaction queryStone = new BankTransaction();
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    boolQueryBuilder.must(QueryBuilders.termQuery("user","stone"));
    List<BankTransaction> stoneResult = ElasticSearchUtil.getInstance().query(queryStone,BankTransaction.class,null,boolQueryBuilder);
    BankTransaction getStone = stoneResult.get(0);
    Assert.assertEquals(200,getStone.getAmount());

    BankTransaction bankTransaction3 = new BankTransaction();
    bankTransaction3.setUuid(3L);
    bankTransaction3.setUser("xf");
    bankTransaction3.setType(1);
    bankTransaction3.setAmount(300);
    bankTransaction3.setScore(30);
    bankTransactionRepository.create(bankTransaction3);

    BankTransaction emptyTransaction = new BankTransaction();
    BoolQueryBuilder aggBoolQueryBuilder = QueryBuilders.boolQuery();
    aggBoolQueryBuilder.must(QueryBuilders.termQuery("user","xf"));
    List<AbstractAggregationBuilder> aggregationBuilders = new ArrayList<>();
    SumBuilder sb = AggregationBuilders.sum("totalAmount").field("amount");
    aggregationBuilders.add(sb);
    Aggregations aggregations = ElasticSearchUtil.getInstance().aggregationQuery(emptyTransaction,null,aggBoolQueryBuilder,aggregationBuilders);
    Sum sum = aggregations.get("totalAmount");
    Assert.assertEquals(400,sum.getValue(),0.001);
  }

}
