package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.ProductFixtures.십원_상품;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class MenuFixtures {

    private MenuFixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static Menu createMenu(
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(price, display, menuProductId, Arrays.asList(menuProducts));
    }

    public static Menu createMenu(
        BigDecimal price,
        String name,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(
            UUID.randomUUID(),
            price,
            name,
            display,
            menuProductId,
            Arrays.asList(menuProducts)
        );
    }

    public static Menu createMenu(
        UUID menuId,
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(
            menuId,
            price,
            "좋은말",
            display,
            menuProductId,
            Arrays.asList(menuProducts)
        );
    }

    public static Menu createMenu(
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        return createMenu(
            UUID.randomUUID(),
            price,
            "좋은말",
            display,
            menuProductId,
            menuProducts
        );
    }

    public static Menu createMenu(
        UUID menuId,
        BigDecimal price,
        String name,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setPrice(price);
        menu.setName(name);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuProductId);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product product) {
        return createMenuProduct(0L, product, 2);
    }

    public static MenuProduct createMenuProduct(
        Long seq,
        Product product,
        long quantity
    ) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuProduct 십원짜리_상품_2개인_menuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(십원_상품());
        return menuProduct;
    }
}
