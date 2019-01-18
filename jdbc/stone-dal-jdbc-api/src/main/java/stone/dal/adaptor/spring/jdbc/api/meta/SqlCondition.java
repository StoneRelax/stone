package stone.dal.adaptor.spring.jdbc.api.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fengxie
 */
public class SqlCondition {

  private Class clazz;

  private List<Object> params = new ArrayList<>();

  private List<String> operators = new ArrayList<>();

  public SqlCondition(Class clazz) {
    this.clazz = clazz;
  }

  public static SqlCondition create(Class clazz) {
    return new SqlCondition(clazz);
  }

  public SqlCondition eq(String field, Object value) {
    params.add(value);
    operators.add(field + "=?");
    return this;
  }

  public SqlCondition gt(String field, Object value) {
    params.add(value);
    operators.add(field + ">?");
    return this;
  }

  public SqlCondition gtAndEq(String field, Object value) {
    params.add(value);
    operators.add(field + ">=?");
    return this;
  }

  public SqlCondition ls(String field, Object value) {
    params.add(value);
    operators.add(field + "<?");
    return this;
  }

  public SqlCondition lsAndeq(String field, Object value) {
    params.add(value);
    operators.add(field + "<=?");
    return this;
  }

  public SqlCondition and() {
    operators.add("and");
    return this;
  }

  public SqlCondition or() {
    operators.add("or");
    return this;
  }

  public SqlCondition join(SqlCondition condition) {
    SqlQueryMeta joinMeta = condition.build();
    operators.add("(" + joinMeta.getSql() + ")");
    params.addAll(Arrays.asList(joinMeta.getParameters()));
    return this;
  }

  public SqlCondition union(SqlCondition condition, String operator){
    SqlQueryMeta joinMeta = condition.build();
    if(operator.toUpperCase().equals("AND")){
      operators.add("AND (" + joinMeta.getSql() + ")");
      params.addAll(Arrays.asList(joinMeta.getParameters()));
    }else if(operator.toUpperCase().equals("OR")){
      operators.add("OR (" + joinMeta.getSql() + ")");
      params.addAll(Arrays.asList(joinMeta.getParameters()));
    }
    return this;
  }

  public SqlQueryMeta build() {
    StringBuilder sb = new StringBuilder();
    operators.forEach(operator -> {
      sb.append(operator);
      sb.append(" ");
    });
    return SqlQueryMeta.factory().mappingClazz(clazz)
        .sql(sb.toString()).params(params.toArray(new Object[0])).build();
  }

}
