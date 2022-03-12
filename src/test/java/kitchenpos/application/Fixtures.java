package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;

public final class Fixtures {

    private Fixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static Product createProduct(BigDecimal price) {
        return createProduct("좋은말", price);
    }

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(UUID productId, BigDecimal price) {
        return createProduct(productId, "좋은말", price);
    }

    public static Product 일원짜리_Product(UUID productId) {
        return createProduct(productId, "좋은말", BigDecimal.ONE);
    }

    public static Product createProduct(UUID productId, String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(productId);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public static Product 십원_상품(UUID productId) {
        return createProduct(productId, "십원짜리", BigDecimal.TEN);
    }

    public static MenuProduct createMenuProduct(Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Menu createMenu(BigDecimal price, boolean display, MenuProduct... menuProducts) {
        final Menu menu = new Menu();
        menu.setPrice(price);
        menu.setDisplayed(display);
        menu.setMenuProducts(
            Arrays.asList(
                menuProducts
            )
        );
        return menu;
    }

    public static OrderTable createOrderTable(
        UUID id,
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    public static OrderTable createOrderTable(
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        return createOrderTable(
            UUID.randomUUID(),
            name,
            numberOfGuests,
            empty
        );
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(null, name);
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
        UUID menuId,
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(menuId, price, display, menuProductId, Arrays.asList(menuProducts));
    }

    public static Menu createMenu(
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        return createMenu(UUID.randomUUID(), price, display, menuProductId, menuProducts);
    }

    public static Menu createMenu(
        UUID menuId,
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setPrice(price);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuProductId);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
