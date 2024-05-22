package kitchenpos.fake.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class TestProductRepository implements ProductRepository {
    @Override
    public Product save(Product product) {
        return product;
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return null;
    }
}
