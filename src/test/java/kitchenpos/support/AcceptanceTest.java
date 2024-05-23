package kitchenpos.support;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.UNDEFINED_PORT;

/**
 * <pre>
 * 인수테스트 환경 설정을 도와주는 클래스
 * 인수테스트를 작성하고자 하려면 해당 클래스를 상속한다.
 * </pre>
 */
@ActiveProfiles("test")
@Import({TestConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @Autowired
    private DatabaseCleanUp databaseCleanup;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

}
