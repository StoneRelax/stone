package stone.dal.seq.spi;

import java.util.Collection;
import stone.dal.seq.api.meta.SequenceMeta;

/**
 * @author fengxie
 */
public interface SequenceMetaLoaderSpi {

	Collection<SequenceMeta> load();
}
