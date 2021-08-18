package kitchenpos.mock;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class MockProductRepository implements ProductRepository {
    private final Map<UUID, Product> productMap = new HashMap<>();

    @Override
    public Product save(final Product product) {
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(final UUID uuid) {
        return Optional.ofNullable(productMap.get(uuid));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public List<Product> findAllById(final List<UUID> uuids) {
        return productMap.values()
                .stream()
                .filter(product -> uuids.contains(product.getId()))
                .collect(Collectors.toList());
    }
}
