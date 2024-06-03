package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static Menu createMenuRequest(MenuGroup menuGroup, BigDecimal price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setMenuGroup(menuGroup);
        request.setPrice(price);
        request.setName(name);
        request.setDisplayed(displayed);
        return request;
    }

    public static Menu createMenuRequest(BigDecimal price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setPrice(price);
        request.setName(name);
        request.setDisplayed(displayed);
        return request;
    }

    public static Menu createMenuRequest(MenuProduct menuProduct, MenuGroup menuGroup, long price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(price));
        request.setName(name);
        request.setMenuProducts(List.of(menuProduct));
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        return request;
    }

    public static Menu createMenu(MenuProduct menuProduct, MenuGroup menuGroup, long price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(price));
        request.setName(name);
        request.setMenuProducts(List.of(menuProduct));
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        return request;
    }
}
