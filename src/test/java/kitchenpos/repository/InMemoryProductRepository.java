package kitchenpos.repository;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    private Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(final List<UUID> ids) {
        return products.values()
                .stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Product save(final Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(final UUID uuid) {
        return Optional.ofNullable(products.get(uuid));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
