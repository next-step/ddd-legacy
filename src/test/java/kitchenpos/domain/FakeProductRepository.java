package kitchenpos.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public Product save(final Product product) {
        this.products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(final UUID id) {
        final Product result = this.products.get(id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Product> findAll() {
        final Collection<Product> results = this.products.values();
        return List.copyOf(results);
    }

    @Override
    public List<Product> findAllByIdIn(final List<UUID> ids) {
        return this.products.values()
                .stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toUnmodifiableList());
    }
}
