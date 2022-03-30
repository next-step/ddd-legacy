package kitchenpos.testdouble;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ProductStubRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public Product save(Product product) {
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

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.values().stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toList());
    }
}
