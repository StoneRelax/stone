package stone.dal.seq.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import stone.dal.seq.api.SequenceGenerator;
import stone.dal.seq.api.SequenceManager;
import stone.dal.seq.api.meta.SequenceMeta;

/**
 * @author fengxie
 */
public class SequenceManagerImpl implements SequenceManager {

	private Map<String, SequenceGenerator> seqGeneratorMap = new HashMap<>();

  public SequenceManagerImpl(String storePath, Collection<SequenceMeta> seqs) {
    SequenceMixGeneratorImpl mixGenerator = new SequenceMixGeneratorImpl(storePath, seqs);
    SequenceSeedGeneratorImpl seedGenerator = new SequenceSeedGeneratorImpl(storePath, seqs);
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
