package kitchenpos.setup;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductSetup {

    private final ProductRepository productRepository;

    public ProductSetup(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product setupProduct(final Product product) {
        return productRepository.save(product);
    }
}
