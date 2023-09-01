package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {
    private Map<UUID, Product> map = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return map.values().stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        map.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<Product> findAll() {
        return null;
    }
}
