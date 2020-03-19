package kitchenpos.model;

import java.math.BigDecimal;

public class ProductTest {
    static final long HALF_FRIED_ID = 1L;
    static final long HALF_CHILLY_ID = 2L;

    public static Product ofHalfFried() {
        final Product product = new Product();
        product.setId(HALF_FRIED_ID);
        product.setPrice(BigDecimal.valueOf(7000));
        product.setName("후라이드 반마리");
        return product;
    }

    public static Product ofHalfChilly() {
        final Product product = new Product();
        product.setId(HALF_CHILLY_ID);
        product.setName("양념 반마리");
        product.setPrice(BigDecimal.valueOf(8000L));
        return product;
    }
}
