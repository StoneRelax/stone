package stone.dal.jdbc.spring.adaptor.impl;

import stone.dal.models.EntityMetaManager;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.EntityMeta;
import stone.dal.seq.api.SequenceManager;
import stone.dal.seq.api.ex.InvalidInputException;
import stone.dal.seq.api.ex.UndefinedSeqException;
import stone.dal.seq.spi.SequenceSpi;

/**
 * @author fengxie
 */
public class JdbcSequenceSpiAdapterImpl implements SequenceSpi {


	private EntityMetaManager entityMetaManager;

	private SequenceManager sequenceManager;

	public JdbcSequenceSpiAdapterImpl(EntityMetaManager entityMetaManager, SequenceManager sequenceManager) {
		this.entityMetaManager = entityMetaManager;
		this.sequenceManager = sequenceManager;
	}

	@SuppressWarnings("unchecked")
	public <T> T next(BaseDo obj, String field) throws UndefinedSeqException,InvalidInputException {
		EntityMeta entity = entityMetaManager.getEntity(obj.getClass());
		String seqId = entity.getClazz().getName() + "." + field;
		return (T) sequenceManager.getGenerator(seqId).next(seqId, obj);
	}
}
