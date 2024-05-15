package kitchenpos.helper;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestHelper {
    private static ProductRepository productRepository;

    public ProductTestHelper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static Product 음식_생성(String name, BigDecimal price){
        UUID id = UUID.randomUUID();

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return productRepository.save(product);
    }
}
