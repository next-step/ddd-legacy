package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    Map<UUID, Product> map = new HashMap<>();

    @Override
    public Product save(Product entity) {
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.ofNullable(map.get(uuid));
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(it -> map.get(it))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
