package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> products = new HashMap();
    @Override
    public List<Product> findAllByIdIn(List<UUID> list) {
        return products.values().stream().
                filter(product -> list.contains(product.getId())).toList();
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
