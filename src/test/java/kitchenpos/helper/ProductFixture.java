package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static final Supplier<Product> FRIED_CHICKEN = () -> product("후라이드 치킨", 6000);

    public static final Supplier<Product> HOT_SPICY_CHICKEN = () -> product("양념 치킨", 6000);

    private static Product product(String name, int price) {
        var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product request(String name, int price) {
        var product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product request(int price) {
        var product = new Product();
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
