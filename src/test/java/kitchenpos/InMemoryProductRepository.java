package kitchenpos;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {
    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.empty();
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public List<Product> findAll() {
        return null;
    }
}
