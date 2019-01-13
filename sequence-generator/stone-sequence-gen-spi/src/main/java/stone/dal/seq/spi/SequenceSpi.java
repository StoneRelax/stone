package stone.dal.seq.spi;

public interface SequenceSpi {
  <T> T next(Object obj, String field) throws Exception;
}
