package stone.dal.jdbc.spring.adaptor.impl;

import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.jdbc.JdbcDclRunner;

public class JdbcDclRunnerImpl implements JdbcDclRunner {

  private JdbcTemplate jdbcTemplate;

  public JdbcDclRunnerImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int run(String sql) {
    return (int) jdbcTemplate.execute(sql, (CallableStatementCallback<Object>) cs -> cs.getResultSet().getRow());
  }
}
