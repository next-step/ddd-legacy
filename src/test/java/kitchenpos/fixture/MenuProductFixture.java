package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuProductFixture {
    private static final long DEFAULT_QUANTITY = 1;
    public static MenuProduct create(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(UUID.randomUUID());
        return menuProduct;
    }

    public static List<MenuProduct> createDefaultsWithPrice(BigDecimal... prices) {
       return Arrays.asList(prices)
                .stream()
                .map(MenuProductFixture::createDefaultWithPrice)
                .collect(Collectors.toList());
    }
    public static List<MenuProduct> createDefaulsWithProduct(Product... products) {
        return Arrays.asList(products)
                .stream()
                .map(MenuProductFixture::createDefaultWithProduct)
                .collect(Collectors.toList());
    }
    private static MenuProduct createDefaultWithPrice(BigDecimal price){
        return create(ProductFixture.create(price), DEFAULT_QUANTITY);
    }
    private static MenuProduct createDefaultWithProduct(Product product){
        return create(product, DEFAULT_QUANTITY);
    }

}
