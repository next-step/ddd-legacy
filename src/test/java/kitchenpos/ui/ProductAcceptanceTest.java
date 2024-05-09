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
import static kitchenpos.fixture.ProductFixture.NAME_강정치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_17000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
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
        Product request = productCreateRequest(NAME_강정치킨, PRICE_17000);

        // when
        ExtractableResponse<Response> response = createProductStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getString("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_강정치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(PRICE_17000)
        );
    }

    @DisplayName("상품의 가격을 수정한다.")
    @Test
    void changePrice() {
        //given
        Product PRODUCT_강정치킨 = createProductStep(productCreateRequest(NAME_강정치킨, PRICE_17000)).as(Product.class);
        Product request = productChangePriceRequest(PRICE_18000);

        // when
        ExtractableResponse<Response> response = changePriceStep(request, PRODUCT_강정치킨.getId());

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_강정치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(PRICE_18000)
        );
    }

    @DisplayName("상품 목록을 볼 수 있다.")
    @Test
    void getProducts() {
        //given
        Product PRODUCT_강정치킨 = createProductStep(productCreateRequest(NAME_강정치킨, PRICE_17000)).as(Product.class);;
        Product PRODUCT_후라이드치킨 = createProductStep(productCreateRequest(NAME_후라이드치킨, PRICE_18000)).as(Product.class);

        // when
        ExtractableResponse<Response> response = getProductsStep();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class)).hasSize(2)
                        .contains(PRODUCT_강정치킨.getId(), PRODUCT_후라이드치킨.getId()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(2)
                        .contains(PRODUCT_강정치킨.getName(), PRODUCT_후라이드치킨.getName()),
                () -> assertThat(response.jsonPath().getList("price")).hasSize(2)
                        .contains(PRODUCT_강정치킨.getPrice().floatValue(), PRODUCT_후라이드치킨.getPrice().floatValue())
        );
    }
}
