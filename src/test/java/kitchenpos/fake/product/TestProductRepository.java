package kitchenpos.fake.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class TestProductRepository implements ProductRepository {
    private final ConcurrentHashMap<UUID, Product> products = new ConcurrentHashMap<>();

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
        return new ArrayList<>(products.values()
                                       .stream()
                                       .filter(it -> ids.contains(it.getId()))
                                       .toList());
    }
}
