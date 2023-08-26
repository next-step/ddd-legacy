package kitchenpos;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

}