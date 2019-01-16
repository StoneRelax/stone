package stone.dal.jdbc.spring.adaptor.aop.example;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.jdbc.spring.adaptor.aop.example.repo.UserRepository;
import stone.dal.jdbc.spring.adaptor.app.SpringJdbcAdaptorTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringJdbcAdaptorTestApplication.class)
public class UserRepositoryTest {

//  @Autowired
//  private UserRepository userRepository;

  @Autowired
  private UserRepository userJpaRepository;

  //todo:call user repository

//  @Test
//  public void testFindUserByManager(){
//    userRepository.findByManager(true);
//  }

  @Test
  public void testJpaFind() {
//    Goods goods = userJpaRepository.findByManager("GOODS_1");
//    Assert.assertEquals("GOODS_1", goods.getName());
  }
}
