package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(int price) {
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "price", new BigDecimal(price));
        return menu;
    }

    public static Menu createMenu(int price, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(price);
        ReflectionTestUtils.setField(menu, "menuProducts", menuProducts);
        return menu;
    }

    public static Menu createMenu(String name, int price, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "name", name);
        ReflectionTestUtils.setField(menu, "price", new BigDecimal(price));
        ReflectionTestUtils.setField(menu, "menuProducts", menuProducts);
        return menu;
    }

    public static Menu createMenu(String name, int price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(name, price, menuProducts);
        ReflectionTestUtils.setField(menu, "displayed", displayed);
        ReflectionTestUtils.setField(menu, "menuGroup", menuGroup);
        return menu;
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        ReflectionTestUtils.setField(menuGroup, "id", id);
        ReflectionTestUtils.setField(menuGroup, "name", name);
        return menuGroup;
    }

    public static MenuProduct createMenuProduct(Product product, Long quantity, UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        ReflectionTestUtils.setField(menuProduct, "product", product);
        ReflectionTestUtils.setField(menuProduct, "quantity", quantity);
        ReflectionTestUtils.setField(menuProduct, "productId", productId);
        return menuProduct;
    }
}
