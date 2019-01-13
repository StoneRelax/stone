package stone.dal.jdbc.spring.adaptor.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcQueryRunner;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

import java.util.List;

public class JdbcQueryRunnerImpl implements JdbcQueryRunner {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcQueryRunnerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List run(SqlQueryMeta queryMeta) {
        return null;
    }

    @Override
    public Page runPagination(SqlQueryMeta queryMeta) {
        return null;
    }

    @Override
    public BaseDo runFind(BaseDo pk) {
        return null;
    }
}
