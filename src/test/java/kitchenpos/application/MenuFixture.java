package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.application.MenuGroupFixture.createMenuGroupRequest;

public class MenuFixture{




    public static Menu createMenuRequest(final long price, UUID menuGroupId) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuGroupId(menuGroupId);
        return request;
    }

    public static Menu createMenuRequest() {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        return createMenuRequest(request.getName(), request.getPrice());
    }

    public static Menu createMenuRequest(final long price) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        return createMenuRequest(request.getName(), request.getPrice());
    }

    public static Menu createMenuRequest(final List<Product> products) {
        final Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));

        return createMenuRequest(products, request.getId());
    }


    public static Menu createMenuRequest(final String name, final BigDecimal price) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(UUID.randomUUID());

        return createMenuRequest(request.getName(), request.getPrice(), request.getMenuGroupId());

    }

    public static Menu createMenuRequest(final String name, final BigDecimal price, final UUID menuGroupId) {
        final Menu request = new Menu();

        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setDisplayed(true);
        request.setMenuGroup(createMenuGroupRequest());
        Product productRequest = createProductRequest();

        request.setMenuProducts(
                Stream.of(productRequest).map(product -> {
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(productRequest);
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }).toList()
        );
        request.setMenuGroupId(menuGroupId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup());

    }

    public static Menu createMenuRequest(final List<Product> products, final UUID menuGroupId) {
        final Menu request = new Menu();

        request.setId(UUID.randomUUID());
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setDisplayed(true);
        request.setMenuGroup(createMenuGroupRequest());
        request.setMenuProducts(
                products.stream().map(product -> {
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(product);
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }).toList()
        );
        request.setMenuGroupId(menuGroupId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup());
    }

    public static Menu createMenuRequest(
            final String name, final BigDecimal price, final boolean displayed
            , final List<MenuProduct> menuProducts, final UUID menuGroupId,
            final UUID id, final MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroupId);
        request.setId(id);
        request.setMenuProducts(menuProducts);

        return request;
    }

    public static Product createProductRequest() {
        final Product request = new Product();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        return request;
    }

    public static Menu createMenu(UUID menuId, long price, boolean displayed, Product product) {
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }
}