package kitchenpos.menu.menu;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.product.ProductFixture.product;

public class MenuFixture {

    public static Menu createMenu(MenuGroup menuGroup, Name name, List<MenuProduct> menuProducts, Price price) {
        return new Menu(UUID.randomUUID(), name, menuGroup, menuProducts, price);
    }

    public static List<MenuProduct> menuProducts(UUID id) {
        return List.of(new MenuProduct(product(id, BigDecimal.ONE), new Quantity(1)));
    }

    public static Menu menu(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        return new Menu(UUID.randomUUID(), new Name("메뉴", false), menuGroup, menuProducts, new Price(BigDecimal.ONE));
    }

    public static Menu 메뉴가격이_메뉴상품합_보다큼(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        return new Menu(UUID.randomUUID(), new Name("메뉴", false), menuGroup, menuProducts, new Price(BigDecimal.valueOf(11)));
    }

    public static Menu 안보이는메뉴(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = menu(menuGroup, menuProducts);
        menu.hide();
        return menu;
    }
}
