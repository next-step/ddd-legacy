package kitchenpos.mock;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class InMemoryProductRepository implements ProductRepository {
    private final ConcurrentMap<UUID, Product> products = new ConcurrentHashMap<>();

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
    public List<Product> findAllById(List<UUID> collect) {
        return collect.stream()
                .filter(id -> products.containsKey(id))
                .map(id -> products.get(id))
                .collect(toList());
    }
}
