package kitchenpos.fixture;

import kitchenpos.domain.Menu;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(String name, BigDecimal price) {
        Menu 메뉴 = new Menu();
        메뉴.setPrice(price);
        메뉴.setName(name);
        메뉴.setId(UUID.randomUUID());
        return 메뉴;
    }

    public static Menu createMenu(BigDecimal price) {
        Menu 메뉴 = new Menu();
        메뉴.setPrice(price);
        메뉴.setId(UUID.randomUUID());
        return 메뉴;
    }

    public static Menu createMenu(String name) {
        Menu 메뉴 = new Menu();
        메뉴.setName(name);
        메뉴.setId(UUID.randomUUID());
        return 메뉴;
    }
}
