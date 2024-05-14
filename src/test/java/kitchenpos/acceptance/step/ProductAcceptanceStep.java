package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.util.UUID;

public class ProductAcceptanceStep {
    public static Response create(Product product) {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(product)
                .when()
                    .post("/api/products")
                .then()
                    .extract()
                    .response();
        // @formatter:on
    }

    public static Response changePrice(UUID productId, Product product) {
        // @formatter:off
        return RestAssured
                .given()
                .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("productId", productId)
                    .body(product)
                .when()
                    .put("/api/products/{productId}/price")
                .then()
                    .extract()
                    .response();
        // @formatter:on
    }

    public static Response findAll() {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/products")
                .then()
                    .extract()
                    .response();
        // @formatter:on
    }
}
