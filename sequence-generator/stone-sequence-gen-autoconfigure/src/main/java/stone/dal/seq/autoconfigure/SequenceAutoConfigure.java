package stone.dal.seq.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.seq.api.SequenceManager;
import stone.dal.seq.impl.SequenceManagerImpl;
import stone.dal.seq.spi.SequenceMetaLoaderSpi;

/**
 * @author fengxie
 */
@Configuration
public class SequenceAutoConfigure {

  @Autowired
  private SequenceMetaLoaderSpi sequenceMetaLoader;

  @Value("${stone.dal.seq.storePath}")
  private String storePath;

  public SequenceAutoConfigure() {
    System.out.println("init sequence");
  }

  @Bean
  public SequenceManager getSequenceManager() {
    return new SequenceManagerImpl(storePath, sequenceMetaLoader.load());
  }
}
