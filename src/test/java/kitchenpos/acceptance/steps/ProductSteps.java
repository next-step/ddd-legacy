package kitchenpos.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductSteps {

    private static final String URI = "/api/products";

    public static ExtractableResponse<Response> 상품을_생성한다(String name, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품_가격을_바꾼다(UUID productId, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(URI + "/{productId}/price", productId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품_전체를_조회한다() {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URI)
                .then().log().all().extract();
    }
}

