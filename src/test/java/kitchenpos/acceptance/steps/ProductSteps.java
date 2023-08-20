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

    public static ExtractableResponse<Response> 상품_생성(String name, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/products/")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품_가격_수정(UUID productId, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put("/api/products/{productId}/price", productId)
                .then().log().all().extract();
    }
}
