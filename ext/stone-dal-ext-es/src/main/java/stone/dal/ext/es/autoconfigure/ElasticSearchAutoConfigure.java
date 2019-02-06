package stone.dal.ext.es.autoconfigure;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import stone.dal.ext.es.adaptor.ElasticSearchAdaptor;

@Configuration
public class ElasticSearchAutoConfigure {

  @Bean
  public ElasticSearchAdaptor getEsAdaptor(Client client) {
    return new ElasticSearchAdaptor(new ElasticsearchTemplate(client));
  }
}


