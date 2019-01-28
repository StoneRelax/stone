package ${packageName};

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.${doClass};

public interface ${className} extends StJpaRepository<${doClass}, Long> {


}