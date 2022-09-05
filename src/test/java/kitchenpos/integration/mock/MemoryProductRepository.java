package kitchenpos.integration.mock;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemoryProductRepository implements ProductRepository {

    private static final Map<UUID, Product> STORE = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return STORE.values()
                .stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        if (!STORE.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(STORE.get(id));
    }

    @Override
    public Product save(Product product) {
        STORE.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(STORE.values());
    }

    public void clear() {
        STORE.clear();
    }
}
