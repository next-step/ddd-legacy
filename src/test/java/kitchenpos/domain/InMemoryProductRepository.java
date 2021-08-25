package kitchenpos.domain;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {

    Map<UUID, Product> products;

    public InMemoryProductRepository() {
        products = new HashMap<>();
    }

    @Override
    public Product save(Product product) {
        product.setId(UUID.randomUUID());
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.ofNullable(products.get(uuid));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
