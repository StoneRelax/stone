package stone.dal.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.kernel.utils.ObjectUtils;
import stone.dal.models.meta.EntityMeta;
import stone.dal.models.meta.FieldMeta;

import static stone.dal.kernel.utils.KernelUtils.bool_v;
import static stone.dal.kernel.utils.KernelUtils.str_emp;

/**
 * @author fengxie
 */
public abstract class DalEntity {

	protected EntityMeta meta;

	protected HashMap<String, List<String>> uniqueKeys = new HashMap<>();
	protected HashMap<String, FieldMeta> fieldMapper = new HashMap<>();
	protected HashSet<String> pks = new HashSet<>();
	protected HashSet<String> seqFields = new HashSet<>();
	protected HashSet<String> seqGenerators = new HashSet<>();
	protected HashMap<String, List<String>> indices = new HashMap<>();
	protected Class pkClass;
	private static Logger logger = LoggerFactory.getLogger(DalEntity.class);


	public DalEntity(EntityMeta meta) {
		this.meta = meta;
		doInit();
		readEntityMeta(meta);
	}

	protected abstract void doInit();

	protected void readEntityMeta(EntityMeta meta) {
		meta.getFields().forEach(field -> {
			readCommonFieldInfo(field);
			readFieldInfo(field);
		});
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
		if (bool_v(field.getPk())) {
			pks.add(field.getName());
		}
		if (!str_emp(field.getSeqKey())) {
			seqFields.add(field.getName());
		}
		if (!str_emp(field.getSeqType())) {
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

	protected abstract void readFieldInfo(FieldMeta fieldMeta);

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
