package kitchenpos.ui.requestor;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.util.UUID;

public class ProductRequestor {

    private static final String DEFAULT_URL = "/api/products";

    public static ExtractableResponse<Response> 상품생성요청(Product product) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static UUID 상품생성요청_상품식별번호반환(Product product) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("id", UUID.class);
    }

    public static ExtractableResponse<Response> 상품가격변경요청(UUID productId, Product product) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("productId", productId)
                .body(product)
                .when().put(DEFAULT_URL + "/{productId}/price")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 상품전체조회요청() {
        return RestAssured.given().log().all()
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

}
