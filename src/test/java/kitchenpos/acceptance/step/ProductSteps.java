package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {
    private static final String ENDPOINT = "/api/products";

    public static ExtractableResponse<Response> 상품_등록_요청(String name, int price) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createParams(name, price))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 가격_수정_요청(ExtractableResponse<Response> createResponse, int price) {
        String id = createResponse.body().jsonPath().getString("id");
        String name = createResponse.body().jsonPath().getString("name");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createParams(name, price))
                .when().put(ENDPOINT + "/{productId}/price", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT)
                .then().log().all().extract();
    }

    public static void 상품_등록_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 가격_수정_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 상품_목록_조회_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static HashMap<String, String> createParams(String name, int price) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price + "");
        return params;
    }
}
