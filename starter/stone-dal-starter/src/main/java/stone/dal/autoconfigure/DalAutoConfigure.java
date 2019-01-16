package stone.dal.autoconfigure;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.ex.DoParseException;
import stone.dal.models.EntityMetaManager;
import stone.dal.seq.api.meta.SequenceMeta;
import stone.dal.seq.spi.SequenceMetaLoaderSpi;

@Configuration
public class DalAutoConfigure {

  @Value("${stone.dal.scan.packages}")
  private String scanPackages;

  @Bean
  public EntityMetaManager getEntityMetaManager() throws DoParseException {
    return new EntityMetaManager(scanPackages.split(","));
  }

  @Bean
  public SequenceMetaLoaderSpi getSequenceMetaLoaderSpi() {
    return new SequenceMetaLoaderSpi() {
      @Override
      public Collection<SequenceMeta> load() {
        return new ArrayList<>();
      }
    };
  }
}