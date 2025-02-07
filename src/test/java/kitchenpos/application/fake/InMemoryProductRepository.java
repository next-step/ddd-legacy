package kitchenpos.application.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {
    private Map<UUID, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.values().stream()
                .filter(product -> ids.contains(product.getId()))
                .toList();
    }
}
