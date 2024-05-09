package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static final String NAME_순살치킨 = "순살치킨";
    public static final String NAME_반반치킨 = "반반치킨";
    public static final BigDecimal PRICE_19000 = BigDecimal.valueOf(19_000);
    public static final BigDecimal PRICE_32000 = BigDecimal.valueOf(32_000);
    public static final BigDecimal PRICE_35000 = BigDecimal.valueOf(35_000);

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
