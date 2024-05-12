package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.acceptacne.steps.ProductSteps.changePriceStep;
import static kitchenpos.acceptacne.steps.ProductSteps.createProductStep;
import static kitchenpos.acceptacne.steps.ProductSteps.getProductsStep;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.productChangePriceRequest;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품 인수테스트")
@AcceptanceTest
class ProductAcceptanceTest {

    @DisplayName("상품을 등록한다.")
    @Test
    void createProduct() {
        // given
        Product request = productCreateRequest(이름_양념치킨, 가격_20000);

        // when
        ExtractableResponse<Response> response = createProductStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getString("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_양념치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_20000)
        );
    }

    @DisplayName("상품의 가격을 수정한다.")
    @Test
    void changePrice() {
        //given
        UUID productId = createProductId(이름_양념치킨, 가격_20000);
        Product request = productChangePriceRequest(가격_18000);

        // when
        ExtractableResponse<Response> response = changePriceStep(productId, request);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_양념치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_18000)
        );
    }

    @DisplayName("상품 목록을 볼 수 있다.")
    @Test
    void getProducts() {
        //given
        UUID PRODUCT_양념치킨 = createProductId(이름_양념치킨, 가격_20000);
        UUID PRODUCT_후라이드치킨_ID = createProductId(이름_후라이드치킨, 가격_18000);

        // when
        ExtractableResponse<Response> response = getProductsStep();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .containsExactly(PRODUCT_양념치킨, PRODUCT_후라이드치킨_ID),
                () -> assertThat(response.jsonPath().getList("name"))
                        .containsExactly(이름_양념치킨, 이름_후라이드치킨),
                () -> assertThat(response.jsonPath().getList("price"))
                        .containsExactly(가격_20000.floatValue(), 가격_18000.floatValue())
        );
    }

    private static UUID createProductId(String name, BigDecimal price) {
        return createProductStep(productCreateRequest(name, price)).as(Product.class).getId();
    }
}
