package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuTestFixture {
    private static final String ANY_MENU_NAME = "메뉴1";
    private static final long ANY_MENU_PRICE = 2000L;
    private static final UUID ANY_MENU_GROUP_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final MenuProduct ANY_PRODUCT_WITH_QUANTITY = createMenuProduct(UUID.fromString("22222222-2222-2222-2222-222222222222"), 1);
    static final UUID COFFEE_GROUP_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID AMERICANO_PRODUCT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    static final UUID LATTE_PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    static final UUID COFFEE_SET_MENU_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    static Product americano2000Won() {
        final var americano = new Product();
        americano.setId(AMERICANO_PRODUCT_ID);
        americano.setPrice(new BigDecimal(2000L));
        americano.setName("아메리카노");
        return americano;
    }

    static Product latte3000Won() {
        final var latte = new Product();
        latte.setId(LATTE_PRODUCT_ID);
        latte.setPrice(new BigDecimal(3000L));
        latte.setName("카페라떼");
        return latte;
    }

    static MenuGroup coffeeMenuGroup() {
        final var group = new MenuGroup();
        group.setId(COFFEE_GROUP_ID);
        group.setName("커피");
        return group;
    }

    static Menu coffeeSetMenu(long menuPrice, boolean displayed) {
        final var coffeeSet = new Menu();
        final var coffeeMenus = List.of(
                createMenuProduct(americano2000Won(), 1),
                createMenuProduct(latte3000Won(), 1)
        );
        coffeeSet.setId(COFFEE_SET_MENU_ID);
        coffeeSet.setName("커피 세트");
        coffeeSet.setMenuGroup(coffeeMenuGroup());
        coffeeSet.setMenuProducts(coffeeMenus);
        coffeeSet.setDisplayed(true);
        coffeeSet.setPrice(BigDecimal.valueOf(menuPrice));
        return coffeeSet;
    }

    static Menu coffeeSetMenu(boolean displayed) {
        return coffeeSetMenu(2_000, displayed);
    }

    static Menu coffeeSetMenu(long menuPrice) {
        return coffeeSetMenu(menuPrice, true);
    }

    static Menu request(
            String name,
            BigDecimal price,
            UUID menuGroupId,
            MenuProduct... menuProducts
    ) {
        final var menu = new Menu();

        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setName(name);
        menu.setMenuProducts(Arrays.asList(menuProducts));

        return menu;
    }

    static Menu request(
            String name,
            long price,
            UUID menuGroupId,
            UUID productId,
            int productQuantity
    ) {
        return request(name, BigDecimal.valueOf(price), menuGroupId, createMenuProduct(productId, productQuantity));
    }

    static Menu requestOfPrice(long price) {
        return requestOfPrice(new BigDecimal(price));
    }

    static Menu requestOfPrice(BigDecimal price) {
        return request(
                ANY_MENU_NAME,
                price,
                ANY_MENU_GROUP_ID,
                ANY_PRODUCT_WITH_QUANTITY
        );
    }

    static Menu requestOfName(String name) {
        return request(
                name,
                new BigDecimal(ANY_MENU_PRICE),
                ANY_MENU_GROUP_ID,
                ANY_PRODUCT_WITH_QUANTITY
        );
    }

    static Menu requestOfMenuGroup(UUID menuGroupId) {
        return request(
                ANY_MENU_NAME,
                BigDecimal.valueOf(ANY_MENU_PRICE),
                menuGroupId,
                ANY_PRODUCT_WITH_QUANTITY
        );
    }

    static Menu requestOfProductWithQuantity(UUID productId, int quantity) {
        return request(
                ANY_MENU_NAME,
                ANY_MENU_PRICE,
                ANY_MENU_GROUP_ID,
                productId,
                quantity
        );
    }

    static Menu requestOfProductWithQuantity(UUID product1Id, int product1Quantity, UUID product2Id, int product2Quantity) {
        return request(
                ANY_MENU_NAME,
                BigDecimal.valueOf(ANY_MENU_PRICE),
                ANY_MENU_GROUP_ID,
                createMenuProduct(product1Id, product1Quantity),
                createMenuProduct(product2Id, product2Quantity)
        );
    }

    static Menu requestEmptyProduct() {
        return request(
                ANY_MENU_NAME,
                BigDecimal.valueOf(ANY_MENU_PRICE),
                ANY_MENU_GROUP_ID
        );
    }

    static MenuProduct createMenuProduct(Product product, int quantity) {
        final var menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    static MenuProduct createMenuProduct(UUID productId, int quantity) {
        final var menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
