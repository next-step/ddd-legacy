package kitchenpos.menu.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final Product 떡볶이 = create(UUID.randomUUID(), "떡볶이", new BigDecimal(2000));
    public static final Product 가격_없는_상품 = create(UUID.randomUUID(), "음식", null);
    public static final Product 가격_음수_상품 = create(UUID.randomUUID(), "음식", new BigDecimal(-1));
    public static final Product 부적절한_이름_상품 = create(UUID.randomUUID(), "fuck", new BigDecimal(-1));

    public static Product create(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
