package kitchenpos.ui;

import io.restassured.RestAssured;
import kitchenpos.util.DataBaseClean;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Acceptance {

    @LocalServerPort
    private int port;

    @Autowired
    private DataBaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dataBaseClean.execute();
    }
}
