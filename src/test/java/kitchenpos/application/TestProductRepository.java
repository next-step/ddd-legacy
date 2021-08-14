package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class TestProductRepository implements ProductRepository {
    private final Set<Product> products = new HashSet<>();

    @Override
    public List<Product> findAllById(List<UUID> ids) {
        return products.stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findAny();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        products.add(product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }
}
