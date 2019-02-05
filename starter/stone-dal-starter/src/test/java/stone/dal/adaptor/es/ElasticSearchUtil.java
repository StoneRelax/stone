package stone.dal.adaptor.es;

import org.elasticsearch.action.admin.cluster.validate.template.RenderSearchTemplateAction;
import org.elasticsearch.action.admin.cluster.validate.template.RenderSearchTemplateRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import org.elasticsearch.script.Template;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import stone.dal.common.models.EntityMetaManager;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.kernel.utils.ObjectUtils;


public class ElasticSearchUtil {
    private ElasticsearchTemplate elasticsearchTemplate;
    private EntityMetaManager entityMetaManager ;

    public ElasticSearchUtil(ElasticsearchTemplate elasticsearchTemplate,EntityMetaManager entityMetaManager ){
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityMetaManager = entityMetaManager;
    }

    public void insert(BaseDo obj){
        IndexQuery indexQuery = new IndexQuery();
        EntityMeta entityMeta = entityMetaManager.getEntity(obj.getClass());
        String pkFieldName = "";
        for(FieldMeta fieldMeta : entityMeta.getFields()){
            if(fieldMeta.getPk()){
                pkFieldName = fieldMeta.getName();
                break;
            }
        }
        indexQuery.setIndexName(entityMeta.getTableName());
        indexQuery.setId(ObjectUtils.getPropertyValue(obj,pkFieldName));
        indexQuery.setObject(obj);
        elasticsearchTemplate.index(indexQuery);
    }

    public void pagedQuery(BaseDo obj,int pageSize,int lastIndexId){
//        Client client = elasticsearchTemplate.getClient();
//        Template template = new Template();
//        template.readFrom();
//        SearchResponse searchResponse = new RenderSearchTemplateRequestBuilder(client,RenderSearchTemplateAction.INSTANCE).request().


    }
}
