package kitchenpos.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    Map<UUID, Product> map = new HashMap<>();

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
        return map.values().stream().toList();
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
