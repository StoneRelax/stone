package stone.dal.seq.api;

/**
 * @author fengxie
 */
public interface SequenceManager {

	SequenceGenerator getGenerator(String id);
}
