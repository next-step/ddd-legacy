package kitchenpos.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {
    private final List<Product> products = new ArrayList<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return this.products.stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return this.products.stream()
                .filter(it -> uuid.equals(it.getId()))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        this.products.add(product);
        return this.products.get(products.size()-1);
    }

    @Override
    public List<Product> findAll() {
        return this.products;
    }
}
