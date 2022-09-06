package kitchenpos.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import kitchenpos.utils.DatabaseCleanup;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

  @LocalServerPort
  int port;

  @Autowired
  private DatabaseCleanup databaseCleanup;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    databaseCleanup.execute();
  }
}
