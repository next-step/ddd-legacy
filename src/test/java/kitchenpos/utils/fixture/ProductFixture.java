package kitchenpos.utils.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

import static java.util.UUID.randomUUID;

public class ProductFixture {
    public static Product 상품() {
        final Product product = new Product();
        product.setId(randomUUID());
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(10_000L));
        return product;
    }
}
