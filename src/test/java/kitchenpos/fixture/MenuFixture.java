package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static final String 이름_순살치킨 = "순살치킨";
    public static final String 이름_반반치킨 = "반반치킨";
    public static final BigDecimal 가격_19000 = BigDecimal.valueOf(19_000);
    public static final BigDecimal 가격_34000 = BigDecimal.valueOf(34_000);
    public static final BigDecimal 가격_38000 = BigDecimal.valueOf(38_000);

    static public Menu menuChangePriceRequest(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

    static public Menu menuPriceAndMenuProductResponse(BigDecimal price, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProducts));
        return menu;
    }

    static public Menu menuResponse(String name, BigDecimal price, UUID menuGroupId, boolean displayed, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(List.of(menuProducts));
        return menu;
    }

    static public Menu menuCreateRequest(String name, BigDecimal price, UUID menuGroupId, boolean displayed, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(List.of(menuProducts));
        return menu;
    }
}
