package kitchenpos.product;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.AcceptanceTestSteps.given;

public class ProductSteps {
    public static ExtractableResponse<Response> 상품_생성_요청(String name, int price) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price + "");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/products")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품가격_변경_요청(String path, int price) {
        Map<String, String> params = new HashMap<>();
        params.put("price", price + "");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(path + "/price")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 상품목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/products")
                .then().log().all().extract();
    }
}
