package kitchenpos.product.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

public class ProductionFixture {

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
}
