package stone.dal.spring.es.lib;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.common.models.EntityMetaManager;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ElasticSearchUtil {
    private ElasticsearchTemplate elasticsearchTemplate;
    private EntityMetaManager entityMetaManager ;

    private static final ElasticSearchUtil _INSTANCE = new ElasticSearchUtil();

    public static ElasticSearchUtil getInstance(){
        return _INSTANCE;
    }

    private ElasticSearchUtil(){
        this.elasticsearchTemplate = SpringContextHolder.getBean(ElasticsearchTemplate.class);
        this.entityMetaManager = SpringContextHolder.getBean(EntityMetaManager.class);
    }

    public void add(BaseDo obj){
        IndexQuery indexQuery = new IndexQuery();
        EntityMeta entityMeta = entityMetaManager.getEntity(obj.getClass());
        indexQuery.setIndexName(entityMeta.getTableName());
        String pkFieldName = getPkFiled(entityMeta);
        indexQuery.setId(ObjectUtils.getPropertyValue(obj,pkFieldName).toString());
        indexQuery.setObject(obj);
        elasticsearchTemplate.index(indexQuery);
    }

    public <T> List<T> query(BaseDo obj,Class<T> clazz, SearchType searchType, QueryBuilder queryBuilder){
        EntityMeta entityMeta = entityMetaManager.getEntity(obj.getClass());

        String index = entityMeta.getTableName();
        if(searchType == null){
            searchType = SearchType.DEFAULT;
        }
        NativeSearchQueryBuilder searchQueryBuilder;
        if(queryBuilder == null){
            String pkField = getPkFiled(entityMeta);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery(pkField,ObjectUtils.getPropertyValue(obj,pkField).toString()));
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withIndices(index).withSearchType(searchType);
        }else {
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder).withIndices(index).withSearchType(searchType);
        }
        SearchQuery searchQuery = searchQueryBuilder.build();
        List<T> result = elasticsearchTemplate.queryForList(searchQuery,clazz);
        return result;
    }

    public Aggregations aggregationQuery(BaseDo obj, SearchType searchType, QueryBuilder queryBuilder , List<AbstractAggregationBuilder> abstractAggregationBuilders){
        EntityMeta entityMeta = entityMetaManager.getEntity(obj.getClass());

        String index = entityMeta.getTableName();
        if(searchType == null){
            searchType = SearchType.DEFAULT;
        }
        NativeSearchQueryBuilder searchQueryBuilder;
        if(queryBuilder == null){
            String pkField = getPkFiled(entityMeta);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery(pkField,ObjectUtils.getPropertyValue(obj,pkField).toString()));
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withIndices(index).withSearchType(searchType);
        }else {
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder).withIndices(index).withSearchType(searchType);
        }
        for(AbstractAggregationBuilder aggregationBuilder : abstractAggregationBuilders){
            searchQueryBuilder.addAggregation(aggregationBuilder);
        }
        SearchQuery searchQuery = searchQueryBuilder.build();
        return elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse searchResponse) {
                return searchResponse.getAggregations();
            }
        });
    }



    private String getPkFiled(EntityMeta entityMeta){
        String pkFieldName = "";
        for(FieldMeta fieldMeta : entityMeta.getFields()){
            if(fieldMeta.getPk()){
                pkFieldName = fieldMeta.getName();
                break;
            }
        }
        return pkFieldName;
    }
}
