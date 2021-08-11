package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {
    private final Map<UUID, Product> productMap = new HashMap<>();

    @Override
    public Product save(final Product product) {
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(final UUID productId) {
        return Optional.ofNullable(productMap.get(productId));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public List<Product> findAllById(final List<UUID> productIds) {
        return productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
