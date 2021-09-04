package kitchenpos.utils.fixture;

import kitchenpos.application.InMemoryProductRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.math.BigDecimal;

import static java.util.UUID.randomUUID;

public class ProductFixture {

    public static ProductRepository productRepository = new InMemoryProductRepository();

    public static Product 상품() {
        final Product product = new Product();
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(10_000L));
        return product;
    }

    public static Product 상품저장() {
        final Product product = 상품();
        product.setId(randomUUID());
        return productRepository.save(product);
    }

    public static void 비우기() {
        productRepository = new InMemoryProductRepository();
    }
}
