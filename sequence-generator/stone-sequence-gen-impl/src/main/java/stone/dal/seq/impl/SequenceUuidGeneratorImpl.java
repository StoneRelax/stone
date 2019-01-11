package stone.dal.seq.impl;

import java.util.UUID;
import stone.dal.seq.api.SequenceGenerator;

/**
 * @author fengxie
 */
public class SequenceUuidGeneratorImpl implements SequenceGenerator<String> {

	@Override
	public String next(String seqId, Object context) {
		return UUID.randomUUID().toString();
	}
}
