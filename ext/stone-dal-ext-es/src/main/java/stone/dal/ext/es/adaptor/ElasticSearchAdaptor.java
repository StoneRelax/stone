package stone.dal.ext.es.adaptor;

import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

public class ElasticSearchAdaptor<T> {

  private ElasticsearchTemplate elasticsearchTemplate;

  public ElasticSearchAdaptor(ElasticsearchTemplate elasticsearchTemplate) {
    this.elasticsearchTemplate = elasticsearchTemplate;
  }

  public void insert(T obj) {
    IndexQuery indexQuery = new IndexQuery();
    indexQuery.setObject(obj);
    elasticsearchTemplate.index(indexQuery);
  }

  public T queryById(String id, Class<T> clazz) {
    GetQuery getQuery = new GetQuery();
    getQuery.setId(id);
    return elasticsearchTemplate.queryForObject(getQuery, clazz);

  }

  public List<T> queryForList(Class<T> clazz, SearchType searchType, QueryBuilder queryBuilder,
      SortBuilder sortBuilder, QueryBuilder filterBuilder) {
    if (searchType == null) {
      searchType = SearchType.DEFAULT;
    }
    ElasticsearchPersistentEntity persistentEntity = elasticsearchTemplate.getPersistentEntityFor(clazz);
    NativeSearchQueryBuilder searchQueryBuilder;
    searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder)
        .withIndices(persistentEntity.getIndexName())
        .withSearchType(searchType);
    if(sortBuilder != null){
      searchQueryBuilder.withSort(sortBuilder);
    }
    if(filterBuilder != null){
      searchQueryBuilder.withFilter(filterBuilder);
    }

    SearchQuery searchQuery = searchQueryBuilder.build();
    return elasticsearchTemplate.queryForList(searchQuery, clazz);
  }

  public Page<T> queryForPage(Class<T> clazz, SearchType searchType, QueryBuilder queryBuilder,
      SortBuilder sortBuilder, QueryBuilder filterBuilder, Pageable pageable) {
    if (searchType == null) {
      searchType = SearchType.DEFAULT;
    }
    ElasticsearchPersistentEntity persistentEntity = elasticsearchTemplate.getPersistentEntityFor(clazz);
    NativeSearchQueryBuilder searchQueryBuilder;
    searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder)
            .withIndices(persistentEntity.getIndexName())
            .withSearchType(searchType);
    if(sortBuilder != null){
      searchQueryBuilder.withSort(sortBuilder);
    }
    if(filterBuilder != null){
      searchQueryBuilder.withFilter(filterBuilder);
    }
    if(pageable != null){
      searchQueryBuilder.withPageable(pageable);
    }
    SearchQuery searchQuery = searchQueryBuilder.build();
    return elasticsearchTemplate.queryForPage(searchQuery, clazz);
  }

  public Aggregations aggregationQuery(Class<T> clazz, SearchType searchType, QueryBuilder queryBuilder,
      SortBuilder sortBuilder, QueryBuilder filterBuilder, Pageable pageable,
      List<AbstractAggregationBuilder> aggregationBuilders) {
    if (searchType == null) {
      searchType = SearchType.DEFAULT;
    }
    ElasticsearchPersistentEntity persistentEntity = elasticsearchTemplate.getPersistentEntityFor(clazz);
    NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
    if (queryBuilder != null) {
      searchQueryBuilder = searchQueryBuilder.withQuery(queryBuilder);
    }
    searchQueryBuilder = searchQueryBuilder.withIndices(
        persistentEntity.getIndexName()
    ).withSearchType(searchType);
    for (AbstractAggregationBuilder aggregationBuilder : aggregationBuilders) {
      searchQueryBuilder.addAggregation(aggregationBuilder);
    }
    if(sortBuilder != null){
      searchQueryBuilder.withSort(sortBuilder);
    }
    if(filterBuilder != null){
      searchQueryBuilder.withFilter(filterBuilder);
    }
    if(pageable != null){
      searchQueryBuilder.withPageable(pageable);
    }
    SearchQuery searchQuery = searchQueryBuilder.build();
    return elasticsearchTemplate.query(searchQuery, SearchResponse::getAggregations);
  }

  public void remove(Class<T> clazz, String id) {
    elasticsearchTemplate.delete(clazz, id);
  }

  public void removeByQuery(DeleteQuery deleteQuery) {
    elasticsearchTemplate.delete(deleteQuery);
  }

  public long count(Class<T> clazz, SearchType searchType, QueryBuilder queryBuilder) {
    ElasticsearchPersistentEntity persistentEntity = elasticsearchTemplate.getPersistentEntityFor(clazz);
    if (searchType == null) {
      searchType = SearchType.DEFAULT;
    }
    NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder).withIndices(
        persistentEntity.getIndexName()
    ).withSearchType(searchType);
    SearchQuery searchQuery = searchQueryBuilder.build();
    return elasticsearchTemplate.count(searchQuery);
  }

  public void removeIndex(String indexName) {
    elasticsearchTemplate.deleteIndex(indexName);
  }
  public void createIndex(String indexName) {
    elasticsearchTemplate.createIndex(indexName);
  }

}
