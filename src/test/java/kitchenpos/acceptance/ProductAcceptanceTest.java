package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.config.AcceptanceTest;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class ProductAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("상품을 생성하고 관리하고 조회할 수 있다.")
    @Test
    void productCreateAndManageAndFindAll() {
        Response 후라이드_치킨 = ProductAcceptanceStep.create(createProduct("후라이드 치킨", BigDecimal.valueOf(16000)));
        String productId = 후라이드_치킨.getBody().jsonPath().getString("id");

        Response findAll = ProductAcceptanceStep.findAll();
        assertThat(findAll.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAll.getBody().jsonPath().getList("id")).contains(productId);
        assertThat(findAll.getBody().jsonPath().getList("price", BigDecimal.class)).contains(BigDecimal.valueOf(16000).setScale(1, RoundingMode.HALF_UP));

        ProductAcceptanceStep.changePrice(UUID.fromString(productId), createProduct("후라이드 치킨", BigDecimal.valueOf(17000)));

        findAll = ProductAcceptanceStep.findAll();

        assertThat(findAll.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAll.getBody().jsonPath().getList("id")).contains(productId);
        assertThat(findAll.getBody().jsonPath().getList("price", BigDecimal.class)).contains(BigDecimal.valueOf(17000).setScale(1, RoundingMode.HALF_UP));
    }
}
