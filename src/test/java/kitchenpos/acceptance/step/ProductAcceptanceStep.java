package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAcceptanceStep {
    public static Response create(Product product) {
        // @formatter:off
        Response response = RestAssured
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

        String productId = response.getBody().jsonPath().getString("id");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/api/products/" + productId);
        assertThat(productId).isNotNull();
        assertThat(response.getBody().jsonPath().getString("name")).isEqualTo(product.getName());
        assertThat(response.getBody().jsonPath().getObject("price", BigDecimal.class)).isEqualTo(product.getPrice());

        return response;
    }

    public static Response changePrice(UUID productId, Product product) {
        // @formatter:off
        Response response =  RestAssured
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody().jsonPath().getString("name")).isEqualTo(product.getName());
        assertThat(response.getBody().jsonPath().getObject("price", BigDecimal.class)).isEqualTo(product.getPrice());

        return response;
    }

    public static Response findAll() {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/products")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }
}
