package stone.dal.seq.impl;

import java.util.Collection;
import java.util.HashMap;
import stone.dal.seq.api.SequenceGenerator;
import stone.dal.seq.api.ex.UndefinedSeqException;
import stone.dal.seq.api.meta.SequenceMeta;

/**
 * @author fengxie
 */
public class SequenceSeedGeneratorImpl implements SequenceGenerator<Long> {

	private HashMap<String, SequenceSeed> registry = new HashMap<>();

	SequenceSeedGeneratorImpl(String storePath, Collection<SequenceMeta> definitions) {
		definitions.stream().filter(meta -> meta.getType().equals("seed")).forEach(meta -> {
			registry.put(meta.getId(), new SequenceSeed(storePath, meta));
		});
	}

	@Override
	public Long next(String seqId, Object context) throws UndefinedSeqException {
		SequenceSeed locker = registry.get(seqId);
		if (locker != null) {
			return locker.acquire(locker.getMeta().getStart());
		}
		throw new UndefinedSeqException(seqId + "'s definition is not found!");
	}

}
