package kitchenpos.application.testFixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public record MenuFixture() {

    public static Menu newOne() {
        var menu = new Menu();
        menu.setName("양념치킨");
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

    public static Menu newOne(Product product) {
        var menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(10000));
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOneByProduct(product)));
        return menu;
    }
}
