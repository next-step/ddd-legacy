package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class ProductAcceptanceTest {
    @MockBean
    PurgomalumClient purgomalumClient;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("상품을 생성하고 관리하고 조회할 수 있다.")
    @Test
    void productCreateAndManageAndFindAll() {
        // 상품 생성
        final Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000));
        final Response friedChickenProductResponse = ProductAcceptanceStep.create(
                createProduct("후라이드 치킨", BigDecimal.valueOf(16000))
        );
        final UUID productId = friedChickenProductResponse.getBody().jsonPath().getUUID("id");
        product.setId(productId);

        // 상품 조회
        Response findResponse = ProductAcceptanceStep.findAll();
        assertThat(findResponse.getBody().jsonPath().getList("id", UUID.class)).contains(productId);

        // 가격 변경
        final BigDecimal changePrice = BigDecimal.valueOf(17000).setScale(1, RoundingMode.HALF_UP);
        product.setPrice(changePrice);
        ProductAcceptanceStep.changePrice(productId, product);

        // 상품 조회
        findResponse = ProductAcceptanceStep.findAll();

        assertThat(findResponse.getBody().jsonPath().getList("id", UUID.class)).contains(productId);
        assertThat(findResponse.getBody().jsonPath().getList("price", BigDecimal.class)).contains(changePrice);
    }
}
