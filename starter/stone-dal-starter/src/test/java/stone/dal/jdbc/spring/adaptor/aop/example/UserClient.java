package stone.dal.jdbc.spring.adaptor.aop.example;

import org.springframework.beans.factory.annotation.Autowired;

public class UserClient {

  @Autowired
  private UserRepository userRepository;

  //todo:call user repository
}
