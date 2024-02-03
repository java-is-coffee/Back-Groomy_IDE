package javaiscoffee.groomy.ide.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {
    //JPA 사용 위한 EntityManager 등록
    private final EntityManager em;
    public JpaConfig(EntityManager em) {this.em = em;}

}
