package kitchenpos.unit.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final UUID PRODUCT_ID = UUID.fromString("27c8fc79-d447-4e8a-b03b-0679ea3b1cdd");

    public static final Product 탕수육;
    public static final Product 짜장면;
    public static final Product 짬뽕;
    public static final Product 볶음밥;

    static {
        탕수육 = createProduct("b18579b5-6166-4de4-91b3-b22f5cb1b75d", "탕수육", BigDecimal.valueOf(10000));
        짜장면 = createProduct("27c8fc79-d447-4e8a-b03b-0679ea3b1cdd", "짜장면", BigDecimal.valueOf(5000));
        짬뽕 = createProduct("c7548a8b-4f79-4a3c-bdd8-9323e7ac3289", "짬뽕", BigDecimal.valueOf(6000));
        볶음밥 = createProduct("8c3a35fe-2f5c-4417-8a1b-5c5a99938223", "볶음밥", BigDecimal.valueOf(7000));
    }

    private static Product createProduct(String id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.fromString(id));
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct("", name, price);
    }
}
