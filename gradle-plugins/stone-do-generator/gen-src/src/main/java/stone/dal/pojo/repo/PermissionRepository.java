package stone.dal.pojo.repo;

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.Permission;

public interface PermissionRepository extends StJpaRepository<Permission, Long> {


}