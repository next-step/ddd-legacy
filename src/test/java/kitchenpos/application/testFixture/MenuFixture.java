package kitchenpos.application.testFixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MenuFixture() {

    public static Menu newOne() {
        var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }

    public static Menu newOne(boolean isDisplay) {
        var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(isDisplay);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }

    public static Menu newOne(UUID id) {
        var menu = new Menu();
        menu.setId(id);
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }

    public static Menu newOne(String menuName) {
        var menu = new Menu();
        menu.setName(menuName);
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }

    public static Menu newOne(BigDecimal price) {
        var menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(price);
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }

    public static Menu newOne(int menuPrice, List<Product> products) {
        var menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(menuPrice));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        var menuProducts = products.stream()
                .map(MenuProductFixture::newOne)
                .toList();
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu newOne(Product product) {
        var menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(5000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne(product)));
        return menu;
    }
}
