package kitchenpos.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.keySet()
                .stream()
                .filter(id -> ids.contains(id))
                .map(id -> products.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.ofNullable(products.get(uuid));
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return products.values()
                .stream()
                .collect(Collectors.toList());
    }

}
