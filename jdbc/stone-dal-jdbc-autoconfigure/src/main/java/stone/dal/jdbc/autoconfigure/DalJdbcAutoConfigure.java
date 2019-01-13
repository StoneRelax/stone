package stone.dal.jdbc.autoconfigure;


import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.JdbcQueryRunner;



import java.lang.management.ManagementFactory;

/**
 * @author fengxie
 */
@Configuration
public class DalJdbcAutoConfigure {

}
