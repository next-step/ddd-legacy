package kitchenpos.infra;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

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
        return products.values().stream().toList();
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.values().stream()
            .filter(product -> ids.contains(product.getId()))
            .toList();
    }
}
