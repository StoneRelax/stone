package stone.dal.rdbms.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.sequence.api.SequenceManager;
import stone.dal.rdbms.impl.SequenceManagerImpl;
import stone.dal.rdbms.spi.SequenceMetaLoaderSpi;

/**
 * @author fengxie
 */
@Configuration
public class SequenceAutoConfigure {

	@Autowired
	private SequenceMetaLoaderSpi sequenceMetaLoader;

	@Bean
	public SequenceManager getSequenceManager() {
		return new SequenceManagerImpl(sequenceMetaLoader.load());
	}
}
