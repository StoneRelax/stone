package stone.dal.spi.adapter;

import stone.dal.spi.adapter.seq.DalSequenceSpiAdapter;
import stone.dal.spi.DalSequenceSpi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengxie
 */
@Configuration
public class DalSpiAdapterAutoConfigure {

	@Bean
	public DalSequenceSpi getDalSequenceSpi() {
		return new DalSequenceSpiAdapter();
	}
}
