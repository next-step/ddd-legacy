package kitchenpos.application.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MenuProductFixture {

    public static MenuProduct createMenuProduct(UUID productId, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct, "productId", productId);
        ReflectionTestUtils.setField(menuProduct, "quantity", quantity);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct, "product", product);
        ReflectionTestUtils.setField(menuProduct, "productId", product.getId());
        ReflectionTestUtils.setField(menuProduct, "quantity", quantity);
        return menuProduct;
    }

    private MenuProductFixture() {
    }
}
