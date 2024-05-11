package kitchenpos.application.testFixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;

public record MenuFixture() {
    public static Menu newOneByPrice(BigDecimal price) {
        var menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(price);
        menu.setMenuGroup(MenuGroupFixture.newOne("신메뉴"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(MenuProductFixture.newOne()));
        return menu;
    }
}
