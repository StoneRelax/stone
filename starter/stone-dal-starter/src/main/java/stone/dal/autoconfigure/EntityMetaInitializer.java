package stone.dal.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.Bean;
import stone.dal.common.ex.DoParseException;
import stone.dal.common.models.EntityMetaManager;

public class EntityMetaInitializer {

  @Autowired
  private EntityScanPackages entityScanPackages;

  private EntityMetaManager entityMetaManager;

  @Bean
  public EntityMetaManager getEntityMetaManager() throws DoParseException {
    return this.entityMetaManager = new EntityMetaManager(entityScanPackages.getPackageNames().toArray(new String[0]));
  }
}
