package kitchenpos.product.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductStep {

    private static final String PRODUCT_URL = "/api/products";

    public static ProductSaveRequest createProductSaveRequest(final String name, final int price) {
        return new ProductSaveRequest(name, price);
    }

    public static Product createProduct(String name, int price) {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "name", name);
        ReflectionTestUtils.setField(product, "price", BigDecimal.valueOf(price));
        return product;
    }

    public static MenuProduct createMenuProduct(final Product product, int quantity) {
        MenuProduct menuProduct1 = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct1, "quantity", quantity);
        ReflectionTestUtils.setField(menuProduct1, "product", product);
        return menuProduct1;
    }

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
