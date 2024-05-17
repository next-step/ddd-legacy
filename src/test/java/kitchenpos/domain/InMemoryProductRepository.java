package kitchenpos.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> map = new LinkedHashMap<>();

    @Override
    public Product save(Product entity) {
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.ofNullable(map.get(uuid));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return map.values().stream()
                .filter(product -> ids.contains(product.getId()))
                .toList();
    }
}
