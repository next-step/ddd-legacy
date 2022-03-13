package kitchenpos.application;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static final Product 뿌링클 = new Product();
    public static final Product 맛초킹 = new Product();
    public static final Product 콜라 = new Product();

    static {
        initialize(뿌링클, "뿌링클", 10_000L);
        initialize(맛초킹, "맛초킹", 10_000L);
        initialize(콜라, "콜라", 2_000L);
    }

    private static void initialize(Product product, String name, long price) {
        product.setName(name);
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(price));
    }

}
