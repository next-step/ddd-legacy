package kitchenpos.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MenuFixture {

    private MenuFixture() {

    }

    public static Menu createMenu(
            String name,
            long price
    ) {
        return createMenu(name, price, true);
    }

    public static Menu createMenu(
            String name,
            boolean displayed
    ) {
        return createMenu(name, 10000L, displayed);
    }

    public static Menu createMenu(
            String name,
            Long price,
            boolean displayed
    ) {
        return createMenu(UUID.randomUUID(), name, price, displayed);
    }

    public static Menu createMenu(
            UUID id,
            String name,
            Long price,
            boolean displayed
    ) {
        var menuGroup = MenuGroupFixture.createMenuGroup("메뉴그룹");
        return createMenu(id, name, price, displayed, menuGroup.getId(), menuGroup);
    }

    public static Menu createMenu(
            UUID id,
            String name,
            BigDecimal price,
            boolean displayed
    ) {
        var menuGroup = MenuGroupFixture.createMenuGroup("메뉴그룹");
        return createMenu(id, name, price, displayed, menuGroup.getId(), menuGroup);
    }


    public static Menu createMenu(
            String name,
            Long price,
            boolean displayed,
            MenuGroup menuGroup,
            List<Product> products
    ) {

        List<MenuProduct> menuProducts = IntStream.range(0, products.size())
                .mapToObj(i -> createMenuProduct(products.get(i), i, 1))
                .collect(Collectors.toList());

        return createMenu(UUID.randomUUID(), name, price, displayed, menuGroup.getId(), menuGroup, menuProducts);
    }


    public static Menu createMenu(
            UUID id,
            String name,
            Long price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup
    ) {
        return createMenu(id, name, price, displayed, menuGroupId, menuGroup, Collections.emptyList());
    }

    public static Menu createMenu(
            UUID id,
            String name,
            BigDecimal price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup
    ) {
        return createMenu(id, name, price, displayed, menuGroupId, menuGroup, Collections.emptyList());
    }

    public static Menu createMenu(
            UUID id,
            String name,
            Long price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup,
            List<MenuProduct> menuProducts
    ) {
        var menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu(
            UUID id,
            String name,
            BigDecimal price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup,
            List<MenuProduct> menuProducts
    ) {
        var menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu copy(Menu request) {
        var menu = new Menu();
        menu.setId(request.getId());
        menu.setName(request.getName());
        menu.setPrice(request.getPrice());
        menu.setDisplayed(request.isDisplayed());
        menu.setMenuGroupId(request.getMenuGroupId());
        menu.setMenuGroup(request.getMenuGroup());
        return menu;
    }

    public static MenuProduct createMenuProduct(
            Product product,
            long seq,
            int quantity
    ) {
        var menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
