package stone.dal.sequence.api;

/**
 * @author fengxie
 */
public interface SequenceManager {

	SequenceGenerator getGenerator(String id);
}
