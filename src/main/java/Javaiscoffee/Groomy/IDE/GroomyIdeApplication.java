package Javaiscoffee.Groomy.IDE;

import Javaiscoffee.Groomy.IDE.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	//스프링 시큐리티 기본 로그인 화면 제거
public class GroomyIdeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroomyIdeApplication.class, args);
	}

}
