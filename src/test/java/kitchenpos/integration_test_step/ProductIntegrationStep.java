package kitchenpos.integration_test_step;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.test_fixture.ProductTestFixture;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProductIntegrationStep {
    private final ProductRepository productRepository;

    public ProductIntegrationStep(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createPersistProduct() {
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .changeName("테스트 상품")
                .changePrice(BigDecimal.valueOf(1000))
                .getProduct();
        return productRepository.save(product);
    }
}
