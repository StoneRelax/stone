package stone.dal.jdbc.spring.adaptor.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.models.data.BaseDo;

public class JdbcDmlRunnerImpl implements JdbcDmlRunner {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcDmlRunnerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int run(SqlDmlDclMeta meta) {
        return 0;
    }

    @Override
    public void runInsert(BaseDo obj) {

    }

    @Override
    public void runDelete(BaseDo obj) {

    }

    @Override
    public void runUpdate(BaseDo obj) {

    }
}
