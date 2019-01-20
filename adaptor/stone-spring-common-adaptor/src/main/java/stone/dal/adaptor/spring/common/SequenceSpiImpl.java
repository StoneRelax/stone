package stone.dal.adaptor.spring.common;

import stone.dal.common.spi.SequenceSpi;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.FieldMeta;
import stone.dal.seq.api.SequenceGenerator;
import stone.dal.seq.api.SequenceManager;

public class SequenceSpiImpl implements SequenceSpi {

  private SequenceManager sequenceManager;

  public SequenceSpiImpl(SequenceManager sequenceManager) {
    this.sequenceManager = sequenceManager;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T next(BaseDo obj, FieldMeta meta) {
    try {
      SequenceGenerator generator = sequenceManager.getGenerator(meta.getSeqKey());
      return (T) generator.next(meta.getSeqKey(), obj);
    } catch (Exception ex) {
      //todo:change to specific runtime exception
      throw new KernelRuntimeException(ex);
    }
  }
}
