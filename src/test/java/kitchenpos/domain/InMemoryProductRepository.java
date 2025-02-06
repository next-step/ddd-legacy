package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.values().stream()
            .filter(product -> ids.contains(product.getId()))
            .toList();
    }

    @Override
    public Product save(Product product) {
        final var id = UUID.randomUUID();
        product.setId(id);
        products.put(id, product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}