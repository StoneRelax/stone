package stone.dal.adaptor.spring.jdbc.aop.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import stone.dal.adaptor.spring.common.annotation.EnableSequence;
import stone.dal.adaptor.spring.common.annotation.StRepositoryScan;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
//@ComponentScan(basePackages = { "stone.dal.adaptor.spring.autoconfigure", "stone.dal.adaptor.spring.jdbc.impl",
//        "stone.dal.starter.impl", "stone.dal.impl", "stone.dal.adaptor.spring.jdbc.spring.adaptor.init",
//        "stone.dal.adaptor.spring.autoconfigure","stone.dal.adaptor.spring.jdbc.aop.example" })
@StRepositoryScan("stone.dal.adaptor.spring.jdbc.aop.example.repo")
@EntityScan("stone.dal.common.models")
@EnableSequence()
public class PersonApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonApplication.class, args);
    }
}
