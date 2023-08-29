package kitchenpos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class ProductFakeRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public Optional<Product> findById(final UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAllByIdIn(final List<UUID> ids) {
        return ids.stream()
            .map(products::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAll() {
        return products.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Product save(final Product entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        products.put(entity.getId(), entity);

        return entity;
    }
}
