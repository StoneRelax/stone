package stone.dal.jdbc.spring.adaptor.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDclRunner;

public class JdbcDclRunnerImpl implements JdbcDclRunner {

  private NamedParameterJdbcTemplate jdbcTemplate;

  public JdbcDclRunnerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int run(String sql) {
    return 0;
  }
}
