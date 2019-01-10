package stone.dal.sequence.impl;

import stone.dal.sequence.api.SequenceGenerator;

import java.util.UUID;

/**
 * @author fengxie
 */
public class SequenceUuidGeneratorImpl implements SequenceGenerator<String> {

	@Override
	public String next(String seqId, Object context) {
		return UUID.randomUUID().toString();
	}
}
