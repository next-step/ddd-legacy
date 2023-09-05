package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuProductFixture.*;

public class MenuFixture {

    public static Menu 양념치킨_메뉴(MenuGroup menuGroup, Product product) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName("양념치킨_메뉴");
        menu.setMenuProducts(List.of(양념치킨_메뉴_상품(product)));
        return menu;
    }

    public static Menu 간장치킨_메뉴() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(간장치킨_메뉴_상품()));
        menu.setName("간장치킨_메뉴");
        return menu;
    }


    public static Menu 이름없는_메뉴() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu 메뉴상품_없는_메뉴(MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setName("이름은 있어요");
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu 상품없는_메뉴(MenuGroup menuGroup, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setName("이름은 있어요");
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu 비어있는_메뉴() {
        return new Menu();
    }

    public static Menu 무료_메뉴() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(-1));
        return menu;
    }

    public static Menu 노출된_무료_메뉴() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(-1));
        return menu;
    }

    private static MenuProduct 간장치킨_메뉴_상품() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.간장치킨());
        menuProduct.setQuantity(4L);
        menuProduct.setProductId(UUID.randomUUID());
        return menuProduct;
    }
}
