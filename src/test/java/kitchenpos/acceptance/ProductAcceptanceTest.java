package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kitchenpos.acceptance.ProductSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("제품 관련 기능")
public class ProductAcceptanceTest extends AcceptanceTest {

    @DisplayName("제품을 등록한다")
    @Test
    void addProduct() {
        // when
        제품_등록_요청함(15_000, "후라이드");

        // then
        var 제품_목록 = 제품_목록_조회_요청함();
        제품이_조회됨(제품_목록, "후라이드");
    }

    @DisplayName("제품의 가격을 변경한다.")
    @Test
    void changePrice() {
        // given
        UUID 후라이드 = 제품이_등록됨(15_000, "후라이드");

        // when
        제품의_가격_수정_요청함(후라이드, 10_000);

        // then
        var 제품_목록 = 제품_목록_조회_요청함();
        제품의_가격이_변경됨(제품_목록, 후라이드, 10_000);
    }

    @DisplayName("제품 목록을 조회한다.")
    @Test
    void showProducts() {
        // given
        제품이_등록됨(15_000, "후라이드");
        제품이_등록됨(17_000, "양념");

        // when
        var 제품_목록 = 제품_목록_조회_요청함();

        // then
        제품이_조회됨(제품_목록, "후라이드", "양념");
    }

    private ExtractableResponse<Response> 제품_등록_요청함(final int price, final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("price", String.valueOf(price));
        params.put("name", name);

        ExtractableResponse<Response> response = 제품_등록_요청(given(), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response;
    }

    private ExtractableResponse<Response> 제품_목록_조회_요청함() {
        ExtractableResponse<Response> response = 제품_목록_조회_요청(given());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    private ExtractableResponse<Response> 제품의_가격_수정_요청함(final UUID id, final int price) {
        Map<String, String> params = new HashMap<>();
        params.put("price", String.valueOf(price));

        ExtractableResponse<Response> response = 제품_가격_수정_요청(given(), id, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    private UUID 제품이_등록됨(final int price, final String name) {
        return 제품_등록_요청함(price, name).jsonPath().getUUID("id");
    }

    private void 제품이_조회됨(final ExtractableResponse<Response> response, final String... name) {
        assertThat(response.jsonPath().getList("name", String.class)).contains(name);
    }

    private void 제품의_가격이_변경됨(final ExtractableResponse<Response> response, final UUID id, final int price) {
        List<Map> list = response.jsonPath().get();
        for (Map map : list) {
            compare(map, id, price);
        }
    }

    private void compare(final Map map, final UUID id, final int price) {
        if (id.toString().equals(map.get("id"))) {
            assertThat(Math.round((Float) map.get("price"))).isEqualTo(price);
        }
    }
}
