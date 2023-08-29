package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixtures.메뉴_그룹_등록;
import static kitchenpos.fixture.ProductFixtures.상품_등록;

public class MenuFixtures {

    public static Menu 메뉴_등록() {
        return 메뉴_등록("등심돈까스", 15000, true, 메뉴_그룹_등록(), List.of(메뉴_상품_등록()));
    }

    public static Menu 메뉴_등록(String name, int price, List<MenuProduct> menuProducts) {
        return 메뉴_등록(name, price, true, 메뉴_그룹_등록(), menuProducts);
    }

    public static Menu 메뉴_등록(String name, int price, boolean displayed, List<MenuProduct> menuProducts) {
        return 메뉴_등록(name, price, displayed, 메뉴_그룹_등록(), menuProducts);
    }

    public static Menu 메뉴_등록(final String name, final int price, boolean displayed, final MenuGroup menuGroup,
                             List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }


    public static MenuProduct 메뉴_상품_등록() {
        return 메뉴_상품_등록(상품_등록(), 1L);
    }

    public static MenuProduct 메뉴_상품_등록(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }



    public static Menu 메뉴_등록_요청(String name, int price, boolean displayed, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(new BigDecimal(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuProduct 메뉴_상품_등록_요청(long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
