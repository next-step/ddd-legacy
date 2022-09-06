package kitchenpos.acceptance.test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderFlowAssertions {

    static void 생성_확인됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getString("id")).isNotNull();
    }

    static void 요청에_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    static void 요청에_성공함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void 가격이_일치한다(ExtractableResponse<Response> response, int expected) {
        assertThat(response.jsonPath().getInt("price")).isEqualTo(expected);
    }

    static void 총_개수가_일치한다(ExtractableResponse<Response> response, int expected) {
        assertThat(response.jsonPath().getList("")).hasSize(expected);
    }
}
