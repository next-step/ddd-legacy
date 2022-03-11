package kitchenpos.application.fixture;

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
        탕수육 = createProduct("탕수육", BigDecimal.valueOf(10000));
        짜장면 = createProduct("짜장면", BigDecimal.valueOf(5000));
        짬뽕 = createProduct("짬뽕", BigDecimal.valueOf(6000));
        볶음밥 = createProduct("볶음밥", BigDecimal.valueOf(7000));
    }

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
