package stone.dal.pojo.repo;

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.AdminUser;

public interface AdminUserRepository extends StJpaRepository<AdminUser, Long> {


}