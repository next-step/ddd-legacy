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
import static kitchenpos.application.ProductFixture.createProductRequest;

public class MenuFixture{

    public static Menu createMenuRequest(final long price, final List<MenuProduct> menuProducts) {
        final Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuProducts(menuProducts);
        request.setDisplayed(Display.HIDDEN.getValue());
        return request;
    }

    public static Menu createMenuRequest(final long price, MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuGroupId(menuGroup.getId());
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

    public static Menu createMenuRequest(final String name, final BigDecimal price, final MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroup.getId());
        request.setId(UUID.randomUUID());
        request.setMenuGroup(menuGroup);
        return createMenuRequest(request.getName(), request.getPrice(), request.getMenuGroupId(), request.getId(), request.getMenuGroup());

    }

    public static Menu createMenuRequest(final String name, final BigDecimal price, final MenuGroup menuGroup, final Product product) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroup.getId());
        request.setId(UUID.randomUUID());
        request.setMenuGroup(menuGroup);
        request.setMenuProducts(
                Stream.of(product).map(productRequest -> {
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(productRequest);
                    menuProduct.setQuantity(1L);
                    menuProduct.setProductId(productRequest.getId());
                    menuProduct.setSeq(1L);
                    return menuProduct;
                }).toList());

        request.setDisplayed(true);
        return request;

    }


    public static Menu createMenuRequest(final String name, final BigDecimal price, final UUID menuGroupId, final UUID menuId, final MenuGroup menuGroup) {
        final Menu request = new Menu();

        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setDisplayed(true);
        request.setMenuGroup(menuGroup);
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
        request.setId(menuId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup(), request.getId());

    }


    public static Menu createMenuRequest(final String name, final BigDecimal price) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(UUID.randomUUID());
        request.setId(UUID.randomUUID());
        return createMenuRequest(request.getName(), request.getPrice(), request.getMenuGroupId(), request.getId());

    }

    public static Menu createMenuRequest(final String name, final BigDecimal price, final UUID menuGroupId, final UUID menuId) {
        final Menu request = new Menu();

        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setDisplayed(true);
        request.setMenuGroup(createMenuGroupRequest(menuGroupId));
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
        request.setId(menuId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup(), request.getId());

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
        request.setId(UUID.randomUUID());

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup(), request.getId());
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

    public static Menu createMenuRequest(
            final String name, final BigDecimal price, final boolean displayed
            , final List<MenuProduct> menuProducts, final UUID menuGroupId,
            final UUID id, final MenuGroup menuGroup, final UUID menuId) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroupId);
        request.setId(menuId);
        request.setMenuProducts(menuProducts);

        return request;
    }



    public static Menu createMenu(final UUID menuId, final long price, final boolean displayed, final Product product) {
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

    public static Menu createMenuRequest(final MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuGroup(menuGroup);
        request.setPrice(BigDecimal.valueOf(16_000L));
        return request;

    }

    public static Menu createMenuRequest(final MenuGroup menuGroup, final MenuProduct menuProduct) {
        final Menu request = new Menu();
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuGroup(menuGroup);
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setMenuProducts(List.of(menuProduct));
        return request;
    }

    public static Menu createMenuRequestExceptMenuProduct(final MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuGroup(menuGroup);
        request.setPrice(BigDecimal.valueOf(16_000L));
        return request;
    }

    public static Menu createMenuRequest(final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuGroup(menuGroup);
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setMenuProducts(menuProducts);
        return request;
    }

    public static Menu createMenuRequest(final String name, final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        final Menu request = new Menu();
        request.setName(name);
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuGroup(menuGroup);
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setMenuProducts(menuProducts);
        return request;
    }
}