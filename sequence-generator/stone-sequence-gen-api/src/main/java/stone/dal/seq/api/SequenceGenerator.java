package stone.dal.seq.api;

import stone.dal.seq.api.ex.InvalidInputException;
import stone.dal.seq.api.ex.UndefinedSeqException;

/**
 * @author fengxie
 */
public interface SequenceGenerator<T> {

  T next(String seqId, Object context) throws UndefinedSeqException, InvalidInputException;
}
