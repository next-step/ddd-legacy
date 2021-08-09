package kitchenpos.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductStep {

    public static Product createProduct(String name, int price) {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "name", name);
        ReflectionTestUtils.setField(product, "price", BigDecimal.valueOf(price));
        return product;
    }

    public static Menu createMenu(String name, int price, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "name", name);
        ReflectionTestUtils.setField(menu, "price", new BigDecimal(price));
        ReflectionTestUtils.setField(menu, "menuProducts", menuProducts);
        return menu;
    }

    public static MenuProduct createMenuProduct(final Product product, int quantity) {
        MenuProduct menuProduct1 = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct1, "quantity", quantity);
        ReflectionTestUtils.setField(menuProduct1, "product", product);
        return menuProduct1;
    }

    public static ExtractableResponse<Response> requestCreateProduct(final Product product) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when().post("/api/products")
                .then().log().all().extract();
    }

    public static Product completeCreateProduct(final Product product) {
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
