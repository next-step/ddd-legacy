package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    private Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.values().stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);

        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return products.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findAny();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
