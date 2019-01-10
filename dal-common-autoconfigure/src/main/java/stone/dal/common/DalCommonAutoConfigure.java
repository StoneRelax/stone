package stone.dal.common;

import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.impl.DalEntityMetaManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengxie
 */
@Configuration
public class DalCommonAutoConfigure {

    @Bean
    public DalEntityMetaManager getDalEntityManager() {
        return new DalEntityMetaManagerImpl();
    }
}
