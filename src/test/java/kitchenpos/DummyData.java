package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DummyData {

    public static List<MenuGroup> menuGroups = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<Menu> menus = new ArrayList<>();

    private static final UUID FIRST_ID = UUID.randomUUID();
    private static final UUID SECOND_ID = UUID.randomUUID();

    public DummyData() {
        setMenuGroups();
        setProducts();
        setMenus();
    }

    private void setMenuGroups() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIRST_ID);
        menuGroup.setName("추천메뉴");

        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup.setId(SECOND_ID);
        menuGroup.setName("중식");

        menuGroups.add(menuGroup);
        menuGroups.add(menuGroup2);
    }

    private void setProducts() {
        Product product = new Product();
        product.setId(FIRST_ID);
        product.setPrice(BigDecimal.valueOf(1000));
        product.setName("후라이드");

        Product product2 = new Product();
        product.setId(SECOND_ID);
        product.setPrice(BigDecimal.valueOf(1100));
        product.setName("양념");

        products.add(product);
        products.add(product2);
    }

    private void setMenus() {

    }
}
