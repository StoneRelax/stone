package stone.dal.adaptor.spring.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.common.models.data.Page;
import stone.dal.jdbc.api.meta.SqlBaseMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.spi.JdbcTemplateSpi;

public class JdbcTemplateSpiImpl implements JdbcTemplateSpi {

  private static Logger s_logger = LoggerFactory.getLogger(JdbcTemplateSpiImpl.class);

  private JdbcTemplate jdbcTemplate;

  public JdbcTemplateSpiImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int exec(SqlBaseMeta meta) {
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
    long ts = System.currentTimeMillis();
    List<Page.PageInfo> pageInfo = jdbcTemplate.query(queryMeta.getPageTotalCountQuerySql(), ps -> {
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
    long consume = System.currentTimeMillis() - ts;
    if (consume > 3000) {
      s_logger
          .warn(String.format("Slow Page Total Query:%s, Takes: %s", queryMeta.getPageTotalCountQuerySql(), consume));
    }
    ts = System.currentTimeMillis();
    List rows = jdbcTemplate.query(queryMeta.getPageQuerySql(), ps -> {
      for (int i = 0; i < queryMeta.getParameters().length; i++) {
        setStatementParams(ps, queryMeta.getParameters()[i], i);
      }
    }, (rs, rowNum) -> rowMapper.mapRow(queryMeta, rs.getMetaData(), rowNum, rs));
    consume = System.currentTimeMillis() - ts;
    if (consume > 3000) {
      s_logger.warn(String.format("Slow Query:%s, Takes: %s", queryMeta.getPageTotalCountQuerySql(), consume));
    }
    return new Page(pageInfo.get(0), rows);
  }

}
