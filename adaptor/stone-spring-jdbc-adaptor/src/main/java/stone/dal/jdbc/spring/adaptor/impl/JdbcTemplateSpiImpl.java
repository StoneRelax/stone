package stone.dal.jdbc.spring.adaptor.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.jdbc.JdbcTemplateSpi;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.models.data.Page;

public class JdbcTemplateSpiImpl implements JdbcTemplateSpi {

  private JdbcTemplate jdbcTemplate;

  public JdbcTemplateSpiImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int exec(SqlDmlDclMeta meta) {
    return jdbcTemplate.update(meta.getSql(), meta.getParameters());
  }

  @Override
  public List query(SqlQueryMeta queryMeta) {
    return jdbcTemplate.query(queryMeta.getSql(), ps -> {
      if (queryMeta.getMaxSize() > 0) {
        ps.setMaxRows(queryMeta.getMaxSize());
      }
      for (int i = 0; i < queryMeta.getParameters().length; i++) {
        setStatementParams(ps, queryMeta.getParameters()[i], i);
      }
    }, (rs, rowNum) -> queryMeta.getMapper().mapRow(queryMeta, rs.getMetaData(), rowNum, rs));
  }

  protected void setStatementParams(PreparedStatement ps, Object obj, int index) throws SQLException {
    int _index = index + 1;
    if (obj instanceof Timestamp) {
      ps.setTimestamp(_index, (Timestamp) obj);
    } else if (obj instanceof java.util.Date) {
      java.sql.Date date = new java.sql.Date(((java.util.Date) obj).getTime());
      ps.setDate(_index, date);
    } else if (obj instanceof Long) {
      ps.setLong(_index, (Long) obj);
    } else if (obj instanceof Integer) {
      ps.setInt(_index, (Integer) obj);
    } else if (obj instanceof String) {
      ps.setString(_index, (String) obj);
    } else {
      ps.setObject(_index, obj);
    }
  }

  @Override
  public Page runPagination(SqlQueryMeta queryMeta) {
    return null;
  }
}
