package kitchenpos.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductStep {

    public static Product createProduct(String name, int price) {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "name", name);
        ReflectionTestUtils.setField(product, "price", BigDecimal.valueOf(price));
        return product;
    }

    public static ExtractableResponse<Response> requestCreateProduct(final Product product) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().post("/api/products")
                .then().log().all().extract();
    }

    public static Product completeChangePrice(final Product product) {
        return requestCreateProduct(product).as(Product.class);
    }

    public static ExtractableResponse<Response> requestChangePrice(Product product, UUID id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().put("/api/products/{productId}/price", id)
                .then().log().all().extract();
    }
}
