package stone.dal.adaptor.spring.jdbc.aop.example;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import stone.dal.adaptor.spring.jdbc.aop.example.repo.PersonRepository;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.common.models.Person;


@Component
public class TransactionTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private StJdbcTemplate jdbcTemplate;


    @Transactional(rollbackFor = RuntimeException.class)
    public void save(){

        try {
            Person userStone = new Person();
            userStone.setUuid(1003l);
            userStone.setName("Stone");
            personRepository.create(userStone);

            Person userJacob = new Person();
            userJacob.setUuid(1002l);
            userJacob .setName("Jacob");
            personRepository.create(userJacob);
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
