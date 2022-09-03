package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import kitchenpos.utils.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        databaseCleanUp.execute();
    }

    static RequestSpecification given() {
        return RestAssured
                .given().log().all();
    }
}
