package kitchenpos.product.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import kitchenpos.product.fixture.ProductChangePriceRequest;
import kitchenpos.product.fixture.ProductSaveRequest;
import org.springframework.http.MediaType;

import java.util.UUID;

public class ProductStep {

    private static final String PRODUCT_URL = "/api/products";

    public static ExtractableResponse<Response> requestCreateProduct(final ProductSaveRequest product) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().post(PRODUCT_URL)
                .then().log().all().extract();
    }

    public static Product completeCreateProduct(final ProductSaveRequest product) {
        return requestCreateProduct(product).as(Product.class);
    }

    public static ExtractableResponse<Response> requestChangePrice(ProductChangePriceRequest request, UUID id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put(PRODUCT_URL + "/{productId}/price", id)
                .then().log().all().extract();
    }
}
