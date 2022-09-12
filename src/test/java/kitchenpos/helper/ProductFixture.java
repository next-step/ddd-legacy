package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static final Product FRIED_CHICKEN = create("후라이드 치킨", 6000);

    public static final Product HOT_SPICY_CHICKEN = create("양념 치킨", 6000);

    private static Product create(String name, int price) {
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
