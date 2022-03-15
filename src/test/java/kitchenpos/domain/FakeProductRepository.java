package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> elements = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return elements.values().stream()
            .filter(it -> ids.contains(it.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Product save(Product product) {
        return elements.put(product.getId(), product);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(elements.values());
    }
}
