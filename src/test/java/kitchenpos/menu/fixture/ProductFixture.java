package kitchenpos.menu.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final Product 상품 = create( "상품", new BigDecimal(1000));
    public static final Product 상품_A = create( "상품A", new BigDecimal(1000));
    public static final Product 상품_B = create("상품B", new BigDecimal(500));
    public static final Product 상품_C = create("상품C", new BigDecimal(10000));
    public static final Product 가격_없는_상품 = create(UUID.randomUUID(), "상품", null);
    public static final Product 가격_음수_상품 = create(UUID.randomUUID(), "상품", new BigDecimal(-1));
    public static final Product 부적절한_이름_상품 = create(UUID.randomUUID(), "fuck", new BigDecimal(-1));

    public static Product create(String name, BigDecimal price) {
        return create(UUID.randomUUID(), name, price);
    }

    public static Product create(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
