package stone.dal.autoconfigure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.ex.DoParseException;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.meta.EntityMeta;
import stone.dal.models.meta.FieldMeta;
import stone.dal.seq.api.meta.SequenceMeta;
import stone.dal.seq.spi.SequenceMetaLoaderSpi;

@Configuration
public class  DalAutoConfigure {

  @Autowired
  private EntityScanPackages entityScanPackages;

  private EntityMetaManager entityMetaManager;

  @PostConstruct
  public void init() throws DoParseException {
    this.entityMetaManager = new EntityMetaManager(entityScanPackages.getPackageNames().toArray(new String[0]));
  }

  @Bean
  public EntityMetaManager getEntityMetaManager() throws DoParseException {
    return this.entityMetaManager;
  }

  @Bean
  public SequenceMetaLoaderSpi getSequenceMetaLoaderSpi() {
    return () -> {
      Set<EntityMeta> entityMetas = entityMetaManager.getAllEntities();
      Set<SequenceMeta> sequenceMetaList = new HashSet<>();
      entityMetas.forEach(entity -> {
        Collection<FieldMeta> fields = entity.getFields();
        fields.forEach(field -> {
          if (!StringUtils.isEmpty(field.getSeqKey())) {
            sequenceMetaList.add(SequenceMeta.factory().start(field.getSeqStartNum())
                .step(1).type(field.getSeqType()).id(field.getSeqKey()).build());
          }
        });
      });
      return Collections.unmodifiableCollection(sequenceMetaList);
    };
  }
}
