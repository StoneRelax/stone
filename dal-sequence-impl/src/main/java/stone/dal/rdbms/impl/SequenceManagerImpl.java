package stone.dal.rdbms.impl;

import stone.dal.sequence.api.SequenceGenerator;
import stone.dal.sequence.api.SequenceManager;
import stone.dal.sequence.api.meta.SequenceMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengxie
 */
public class SequenceManagerImpl implements SequenceManager {

	private Map<String, SequenceGenerator> seqGeneratorMap = new HashMap<>();

	public SequenceManagerImpl(Collection<SequenceMeta> seqs) {
		SequenceMixGeneratorImpl mixGenerator = new SequenceMixGeneratorImpl(seqs);
		SequenceSeedGeneratorImpl seedGenerator = new SequenceSeedGeneratorImpl(seqs);
		SequenceUuidGeneratorImpl uuidGenerator = new SequenceUuidGeneratorImpl();
		seqs.forEach(seqMeta -> {
			if ("mix".equals(seqMeta.getType())) {
				seqGeneratorMap.put(seqMeta.getId(), mixGenerator);
			} else if ("seed".equals(seqMeta.getType())) {
				seqGeneratorMap.put(seqMeta.getId(), seedGenerator);
			} else {
				seqGeneratorMap.put(seqMeta.getId(), uuidGenerator);
			}
		});
	}

	@Override
	public SequenceGenerator getGenerator(String id) {
		return seqGeneratorMap.get(id);
	}
}
