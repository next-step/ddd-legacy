package kitchenpos.model;

import java.math.BigDecimal;

public class ProductTest {
    public static Product ofHalfFried() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(7000));
        product.setName("후라이드 반마리");
        return product;
    }

    public static Product ofHalfChilly() {
        Product product = new Product();
        product.setId(2L);
        product.setName("양념 반마리");
        product.setPrice(BigDecimal.valueOf(8000L));
        return product;
    }
}