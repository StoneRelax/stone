package stone.dal.adaptor.spring.autoconfigure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.adaptor.spring.common.SequenceSpiImpl;
import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.adaptor.spring.common.StSequenceConfig;
import stone.dal.common.ex.DoParseException;
import stone.dal.common.models.EntityMetaManager;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.spi.SequenceSpi;
import stone.dal.seq.api.SequenceManager;
import stone.dal.seq.api.meta.SequenceMeta;
import stone.dal.seq.impl.SequenceManagerImpl;

@Configuration
public class DalAutoConfigure {

  private EntityMetaManager entityMetaManager;

  private SequenceManager sequenceManager;

  public DalAutoConfigure(@Autowired EntityScanPackages entityScanPackages,
      @Autowired StSequenceConfig sequenceConfig,
      @Autowired ApplicationContext context) throws DoParseException {
    this.entityMetaManager = new EntityMetaManager(entityScanPackages.getPackageNames().toArray(new String[0]));
    sequenceManager = new SequenceManagerImpl(sequenceConfig.getStorePath(), getSequenceMeta());
    SpringContextHolder.setApplicationContext(context);
  }

  @Bean
  public EntityMetaManager getEntityMetaManager() {
    return this.entityMetaManager;
  }

  @Bean
  public SequenceSpi getSequence() {
    return new SequenceSpiImpl(sequenceManager);
  }

  @Bean
  public SequenceManager getSequenceManager() {
    return sequenceManager;
  }

  private Collection<SequenceMeta> getSequenceMeta() {
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
  }
}