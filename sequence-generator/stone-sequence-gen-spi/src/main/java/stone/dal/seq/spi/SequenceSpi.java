package stone.dal.seq.spi;

import stone.dal.models.data.BaseDo;

public interface SequenceSpi {
    <T> T next(BaseDo obj, String field) throws Exception;
}
