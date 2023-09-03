package kitchenpos.ui;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/db/migration/clean_up_tables.sql")
public class ControllerTest {
    @LocalServerPort
    int port;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
}
