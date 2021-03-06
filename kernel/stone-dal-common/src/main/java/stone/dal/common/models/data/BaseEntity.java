package stone.dal.common.models.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.kernel.utils.ObjectUtils;

public abstract class BaseEntity {
  protected EntityMeta meta;

  protected HashMap<String, FieldMeta> fieldMapper = new HashMap<>();

  protected HashSet<String> pks = new HashSet<>();

  protected HashSet<String> seqFields = new HashSet<>();

  protected HashSet<String> seqGenerators = new HashSet<>();

  protected Class pkClass;

  private static Logger logger = LoggerFactory.getLogger(BaseEntity.class);

  public BaseEntity(EntityMeta meta) {
    this.meta = meta;
    readEntityMeta(meta);
    doInit();
  }

  protected abstract void doInit();

  protected void readEntityMeta(EntityMeta meta) {
    meta.getFields().forEach(this::readCommonFieldInfo);
    if (pks.size() > 1) {
      String pkClazzName = meta.getClazz().getName() + "Pk";
      try {
        Class pkClazz = Class.forName(pkClazzName);
      } catch (ClassNotFoundException e) {
        logger.warn("Primary key class %s is not found!", pkClazzName);
      }
    }
  }

  public Class getPkClass() {
    return pkClass;
  }

  private void readCommonFieldInfo(FieldMeta field) {
    fieldMapper.put(field.getName(), field);
    if (KernelUtils.boolValue(field.getPk())) {
      pks.add(field.getName());
    }
    if (!KernelUtils.isStrEmpty(field.getSeqKey())) {
      seqFields.add(field.getName());
    }
    if (!KernelUtils.isStrEmpty(field.getSeqType())) {
      seqGenerators.add(field.getSeqType());
    }
  }

  public Object[] getPkValues(Object object) {
    List<Object> params = new ArrayList<>();
    for (String pk : pks) {
      params.add(ObjectUtils.getPropertyValue(object, pk));
    }
    return params.toArray(new Object[params.size()]);
  }

  public EntityMeta getMeta() {
    return meta;
  }

  public FieldMeta getField(String name) {
    return fieldMapper.get(name);
  }

  public Collection<String> getPks() {
    return Collections.unmodifiableCollection(pks);
  }

  public Set<String> getSeqFields() {
    return Collections.unmodifiableSet(seqFields);
  }

  public Set<String> getSeqGenerators() {
    return Collections.unmodifiableSet(seqGenerators);
  }

}
