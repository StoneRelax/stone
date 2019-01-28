package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stone.dal.pojo.repo.${repoClass};

@Component
public class ${className} {
  @Autowired
  private ${repoClass} repository;

}
