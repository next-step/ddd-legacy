package kitchenpos.inMemory;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    private Map<UUID, Product> products = new HashMap<>();

    public List<Product> findAllByIdIn(final List<UUID> ids) {
        return products.values()
                .stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    public Product save(final Product product) {
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(final UUID productId) {
        return products.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), productId))
                .findAny();
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
