package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.메뉴_상품_생성;

public class MenuFixture {

    public static Menu 메뉴_생성(String name, BigDecimal price, boolean displayed, UUID menuGroupId, List<Product> products) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        List<MenuProduct> menuProducts = products.stream()
                .map(product -> 메뉴_상품_생성(product, 1L))
                .toList();
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu 메뉴_생성(String name, BigDecimal price, boolean displayed, UUID menuGroupId, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    public static Menu 메뉴_생성(UUID id, boolean displayed, BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setDisplayed(displayed);
        menu.setPrice(price);
        return menu;
    }

    public static MenuGroup 메뉴_그룹_생성(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
