package stone.dal.spi.adapter.seq;

import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.sequence.spi.DalSequenceSpi;
import drone.platform.components.sequence.api.SequenceManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengxie
 */
public class DalSequenceSpiAdapter implements DalSequenceSpi {

	@Autowired
	private DalEntityMetaManager dalEntityMetaManager;
	@Autowired
	private SequenceManager sequenceManager;

	@Override
	@SuppressWarnings("unchecked")
	public <T> T next(DalObj obj, String field) {
		EntityMeta entity = dalEntityMetaManager.getEntity(obj.getClass());
		String seqId = entity.getClazz().getName() + "." + field;
		return (T) sequenceManager.getGenerator(seqId).next(seqId, obj);
	}
}
