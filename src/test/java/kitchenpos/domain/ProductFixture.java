package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProductFixture {
    private ProductFixture() {}

    private static Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static List<Product> createProducts() {
        return Arrays.asList(
                createProduct("첫번째 상품", BigDecimal.valueOf(1_100)),
                createProduct("두번째 상품", BigDecimal.valueOf(2_200)),
                createProduct("세번째 상품", BigDecimal.valueOf(3_300)),
                createProduct("네번째 상품", BigDecimal.valueOf(4_400))
        );
    }

    public static ProductRepository createProductRepository() {
        final ProductRepository productRepository = new FakeProductRepository();
        createProducts().forEach(productRepository::save);
        return productRepository;
    }
}
