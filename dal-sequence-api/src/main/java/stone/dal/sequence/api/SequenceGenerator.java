package stone.dal.sequence.api;

/**
 * @author fengxie
 */
public interface SequenceGenerator<T> {

	T next(String seqId, Object context);
}
