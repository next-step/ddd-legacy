package kitchenpos.fixture;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.math.BigDecimal;

import static java.util.UUID.randomUUID;

public class ProductFixture {
    public static Product 상품() {
        final Product product = new Product();
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000));
        return product;
    }

    public static Product 상품저장(ProductRepository productRepository) {
        final Product product = 상품();
        product.setId(randomUUID());
        return productRepository.save(product);
    }
}
