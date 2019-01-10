package stone.dal.rdbms.impl;

import stone.dal.kernel.KernelRuntimeException;
import stone.dal.sequence.api.SequenceGenerator;
import stone.dal.sequence.api.meta.SequenceMeta;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author fengxie
 */
public class SequenceSeedGeneratorImpl implements SequenceGenerator<Long> {

	private HashMap<String, SequenceSeed> registry = new HashMap<>();

	SequenceSeedGeneratorImpl(Collection<SequenceMeta> definitions) {
		definitions.stream().filter(meta -> meta.getType().equals("seed")).forEach(meta -> {
			registry.put(meta.getId(), new SequenceSeed(meta));
		});
	}

	@Override
	public Long next(String seqId, Object context) {
		SequenceSeed locker = registry.get(seqId);
		if (locker != null) {
			return locker.acquire(locker.getMeta().getStart());
		}
		throw new KernelRuntimeException(seqId + "'s definition is not found!");
	}

}
