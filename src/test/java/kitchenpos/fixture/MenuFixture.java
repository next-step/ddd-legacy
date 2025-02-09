package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static Menu menuWithDisplayTrue(String name, List<MenuProduct> menuProduct, long price, UUID menuGroupId) {
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "name", name);
        ReflectionTestUtils.setField(menu, "menuProducts", menuProduct);
        ReflectionTestUtils.setField(menu, "price", BigDecimal.valueOf(price));
        ReflectionTestUtils.setField(menu, "menuGroupId", menuGroupId);
        ReflectionTestUtils.setField(menu, "displayed", true);
        return menu;
    }

    public static MenuGroup menuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        ReflectionTestUtils.setField(menuGroup, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(menuGroup, "name", name);
        return menuGroup;
    }

    public static MenuProduct menuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct, "productId", product.getId());
        ReflectionTestUtils.setField(menuProduct, "product", product);
        ReflectionTestUtils.setField(menuProduct, "quantity", quantity);
        return menuProduct;
    }
}
