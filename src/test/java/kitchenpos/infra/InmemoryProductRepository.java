package kitchenpos.infra;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductIdGenerator;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.*;

public class InmemoryProductRepository implements ProductRepository {
    private final ProductIdGenerator idGenerator = UUID::randomUUID;
    private final Map<UUID, Product> store = new HashMap<>();

    @Override
    public Product save(Product product) {
        UUID id = idGenerator.generateId();
        product.setId(id);
        store.put(id, product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(store.get(productId));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return store.values().stream()
                .filter(product -> ids.contains(product.getId()))
                .toList();
    }
}