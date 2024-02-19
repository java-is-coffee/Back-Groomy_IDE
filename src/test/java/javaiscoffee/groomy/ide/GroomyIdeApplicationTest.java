package javaiscoffee.groomy.ide;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be",
        "spring.security.oauth2.client.registration.google.client-id=test-client-id",
        "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class GroomyIdeApplicationTest {

    @Test
    public void test() {

    }

}