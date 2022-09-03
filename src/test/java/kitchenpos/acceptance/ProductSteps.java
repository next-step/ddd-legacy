package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

public class ProductSteps {

    public static ExtractableResponse<Response> 제품_등록_요청(final RequestSpecification given, final Map<String, String> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/products")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 제품_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/products")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 제품_가격_수정_요청(final RequestSpecification given, final UUID id, final Map<String, String> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/products/{id}/price", id)
                .then().log().all().extract();
    }
}
