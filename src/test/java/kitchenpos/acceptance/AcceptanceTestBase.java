package kitchenpos.acceptance;

import io.restassured.RestAssured;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AcceptanceTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @Autowired
    public KitchenridersClientDummy kitchenridersClientDummy;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        this.databaseCleanStep.clean();
        this.kitchenridersClientDummy.clearCallCount();
    }
}
