package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuProductFixture {
    private static final long DEFAULT_QUANTITY = 1;

    public static MenuProduct create(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static List<MenuProduct> createDefaultsWithPrice(BigDecimal... prices) {
        return Arrays.stream(prices)
                .map(MenuProductFixture::createDefaultWithPrice)
                .collect(Collectors.toList());
    }

    public static List<MenuProduct> createDefaultsWithProduct(Product... products) {
        return Arrays.stream(products)
                .map(MenuProductFixture::createDefaultWithProduct)
                .collect(Collectors.toList());
    }

    private static MenuProduct createDefaultWithPrice(BigDecimal price) {
        return create(ProductFixture.create(price), DEFAULT_QUANTITY);
    }

    private static MenuProduct createDefaultWithProduct(Product product) {
        return create(product, DEFAULT_QUANTITY);
    }

    public static List<MenuProduct> createDefaults() {
        return createDefaultsWithProduct(
                ProductFixture.createDefault(),
                ProductFixture.createDefault()
        );
    }
}
