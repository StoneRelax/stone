package stone.dal.sequence.spi;

import stone.dal.sequence.api.meta.SequenceMeta;

import java.util.Collection;

/**
 * @author fengxie
 */
public interface SequenceMetaLoaderSpi {

	Collection<SequenceMeta> load();
}
