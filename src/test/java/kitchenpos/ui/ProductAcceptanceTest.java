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

import static kitchenpos.acceptacne.steps.ProductSteps.상품을_등록한다;
import static kitchenpos.acceptacne.steps.ProductSteps.상품의_가격을_수정한다;
import static kitchenpos.acceptacne.steps.ProductSteps.상품의_목록을_보여준다;
import static kitchenpos.fixture.ProductFixture.productChangePriceRequest;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품 인수테스트")
@AcceptanceTest
class ProductAcceptanceTest {
    @DisplayName("[성공] 상품을 등록한다.")
    @Test
    void createProduct() {
        // given
        Product 양념치킨_등록_요청 = 상품의_등록을_요청(이름_양념치킨, 가격_20000);

        // when
        var 양념치킨_등록_응답 = 상품을_등록한다(양념치킨_등록_요청);

        // then
        상품의_등록을_검증한다(양념치킨_등록_응답);
    }

    @DisplayName("[성공] 상품의 가격을 수정한다.")
    @Test
    void changePrice() {
        //given
        UUID productId = 상품을_등록하고_상품Id를_반환한다(이름_양념치킨, 가격_20000);
        Product 양념치킨_가격_수정_요청 = 상품의_가격_수정_요청(가격_18000);

        // when
        var 양념치킨_가격_수정_응답 = 상품의_가격을_수정한다(productId, 양념치킨_가격_수정_요청);

        //then
        상품의_가격이_수정되었는지_검증한다(양념치킨_가격_수정_응답);
    }

    @DisplayName("[성공] 상품 목록을 볼 수 있다.")
    @Test
    void getProducts() {
        //given
        UUID 양념치킨Id = 상품을_등록하고_상품Id를_반환한다(이름_양념치킨, 가격_20000);
        UUID 후라이드치킨Id = 상품을_등록하고_상품Id를_반환한다(이름_후라이드치킨, 가격_18000);

        // when
        var 상품_목록_응답 = 상품의_목록을_보여준다();

        //then
        상품_목록을_검증한다(양념치킨Id, 후라이드치킨Id, 상품_목록_응답);
    }

    private static Product 상품의_가격_수정_요청(BigDecimal price) {
        return productChangePriceRequest(price);
    }

    private static Product 상품의_등록을_요청(String name, BigDecimal price) {
        return productCreateRequest(name, price);
    }

    private static UUID 상품을_등록하고_상품Id를_반환한다(String name, BigDecimal price) {
        return 상품을_등록한다(상품의_등록을_요청(name, price)).as(Product.class).getId();
    }

    private static void 상품의_등록을_검증한다(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getString("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_양념치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_20000)
        );
    }

    private static void 상품의_가격이_수정되었는지_검증한다(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_양념치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_18000)
        );
    }

    private static void 상품_목록을_검증한다(UUID 양념치킨Id, UUID 후라이드치킨Id, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .containsExactly(양념치킨Id, 후라이드치킨Id),
                () -> assertThat(response.jsonPath().getList("name"))
                        .containsExactly(이름_양념치킨, 이름_후라이드치킨),
                () -> assertThat(response.jsonPath().getList("price"))
                        .containsExactly(가격_20000.floatValue(), 가격_18000.floatValue())
        );
    }
}
