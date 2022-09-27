package kitchenpos.application.fakeobject;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeProductRepository implements ProductRepository {
    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }
}
