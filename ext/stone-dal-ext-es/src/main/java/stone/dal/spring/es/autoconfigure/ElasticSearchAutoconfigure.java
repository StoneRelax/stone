package stone.dal.spring.es.autoconfigure;

import stone.dal.spring.es.lib.ElasticSearchUtil;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import stone.dal.common.models.EntityMetaManager;

@Configuration
public class ElasticSearchAutoconfigure {
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(Client client) {
        return new ElasticsearchTemplate(client);
    }
}
