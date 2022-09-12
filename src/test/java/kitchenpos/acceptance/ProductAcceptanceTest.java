package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.acceptance.ProductSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("제품 관련 기능")
public class ProductAcceptanceTest extends AcceptanceTest {

    @DisplayName("제품을 등록한다")
    @Test
    void addProduct() {
        // when
        제품_등록_요청함("후라이드", 15_000L);

        // then
        var 제품_목록 = 제품_목록_조회_요청함();
        제품이_조회됨(제품_목록, "후라이드");
    }

    @DisplayName("제품의 가격을 변경한다.")
    @Test
    void changePrice() {
        // given
        UUID 후라이드 = 제품이_등록됨(15_000L, "후라이드");

        // when
        제품의_가격_수정_요청함(후라이드, 10_000L);

        // then
        var 제품_목록 = 제품_목록_조회_요청함();
        제품의_가격이_변경됨(제품_목록, 후라이드, 10_000L);
    }

    @DisplayName("제품 목록을 조회한다.")
    @Test
    void showProducts() {
        // given
        제품이_등록됨(15_000L, "후라이드");
        제품이_등록됨(17_000L, "양념");

        // when
        var 제품_목록 = 제품_목록_조회_요청함();

        // then
        제품이_조회됨(제품_목록, "후라이드", "양념");
    }

    private ExtractableResponse<Response> 제품_등록_요청함(final String name, final Long price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));

        return 제품_등록_요청(given(), product);
    }

    private ExtractableResponse<Response> 제품_목록_조회_요청함() {
        return 제품_목록_조회_요청(given());
    }

    private ExtractableResponse<Response> 제품의_가격_수정_요청함(final UUID id, final Long price) {
        final Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));

        return 제품_가격_수정_요청(given(), id, product);
    }

    private UUID 제품이_등록됨(final Long price, final String name) {
        return 제품_등록_요청함(name, price).jsonPath().getUUID("id");
    }

    private void 제품이_조회됨(final ExtractableResponse<Response> response, final String... name) {
        assertThat(response.jsonPath().getList("name", String.class)).contains(name);
    }

    private void 제품의_가격이_변경됨(final ExtractableResponse<Response> response, final UUID id, final Long price) {
        List<Product> products = response.jsonPath().getList("", Product.class);
        products.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.getPrice().longValue()).isEqualTo(price));
    }
}
