package kitchenpos.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProductFakeRepository implements ProductRepository {

    Map<UUID, Product> instancePocket;

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return List.of();
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
        return List.of();
    }
}
