package stone.dal.adaptor.spring.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.adaptor.spring.jdbc.spi.JdbcTemplateSpi;
import stone.dal.common.models.data.Page;

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
  public List query(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper) {
    return jdbcTemplate.query(queryMeta.getSql(), ps -> {
      if (queryMeta.getMaxSize() > 0) {
        ps.setMaxRows(queryMeta.getMaxSize());
      }
      for (int i = 0; i < queryMeta.getParameters().length; i++) {
        setStatementParams(ps, queryMeta.getParameters()[i], i);
      }
    }, (rs, rowNum) -> rowMapper.mapRow(queryMeta, rs.getMetaData(), rowNum, rs));
  }

  private void setStatementParams(PreparedStatement ps, Object obj, int index) throws SQLException {
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
  @SuppressWarnings("unchecked")
  public Page queryPage(SqlQueryMeta queryMeta, SqlQueryMeta.RowMapper rowMapper) {
    List<Page.PageInfo> pageInfo = jdbcTemplate.query(queryMeta.getSql(), ps -> {
      ps.setMaxRows(1);
      for (int i = 0; i < queryMeta.getParameters().length; i++) {
        setStatementParams(ps, queryMeta.getParameters()[i], i);
      }
    }, (rs, rowNum) -> {
      int totalCount = rs.getBigDecimal(1).intValue();
      int pageSize = queryMeta.getPageSize();
      int totalPage = (totalCount + pageSize - 1) / pageSize;
      int pageNo = queryMeta.getPageNo();
      if (totalCount > 0) {
        if (totalPage > queryMeta.getPageNo()) {
          pageNo = queryMeta.getPageNo();
        } else {
          pageNo = totalPage;
        }
      }
      return Page.createInfo(pageNo, totalPage, totalCount);
    });
    List rows = jdbcTemplate.query(queryMeta.getPageQuerySql(), ps -> {
      for (int i = 0; i < queryMeta.getParameters().length; i++) {
        setStatementParams(ps, queryMeta.getParameters()[i], i);
      }
    }, (rs, rowNum) -> rowMapper.mapRow(queryMeta, rs.getMetaData(), rowNum, rs));
    return new Page(pageInfo.get(0), rows);
  }


}
