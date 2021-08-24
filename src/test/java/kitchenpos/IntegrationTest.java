package kitchenpos;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }
}
