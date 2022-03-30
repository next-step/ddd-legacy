package kitchenpos.ui.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.util.UUID;

public class ProductStep {

    private static final String PATH = "/api/products";

    public static ExtractableResponse<Response> 제품_생성_요청(Product param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 제품_가격_변경_요청(UUID id, Product param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{productId}/price", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_제품_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }
}
