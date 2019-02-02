package stone.dal.ext.filer.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.ext.filer.FileResolver;
import stone.dal.ext.filer.impl.LocalFileResolver;

@Configuration
public class FilerAutoConfigure {

  @Value("${stone.dal.ext.filer.local-store-path}")
  private String localStorePath;

  @Bean
  public FileResolver getFileResolver() {
    //todo: consider the case when we have second filer solution
    return new LocalFileResolver(localStorePath);
  }

}
