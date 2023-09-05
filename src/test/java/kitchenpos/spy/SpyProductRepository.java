package kitchenpos.spy;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.UUID;

public interface SpyProductRepository extends ProductRepository {

    default <T extends Product> T save(T product) {
        product.setId(UUID.randomUUID());
        return product;
    }
}
