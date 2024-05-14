package kitchenpos;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository implements ProductRepository {

    private final HashMap<UUID, Product> products = new HashMap<>();

    @Override
    public Product save(Product entity) {
        products.put(UUID.randomUUID(), entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
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