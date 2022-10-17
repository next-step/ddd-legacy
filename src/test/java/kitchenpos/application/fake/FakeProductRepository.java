package kitchenpos.application.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> productMap = new HashMap<>();
    @Override
    public Product save(Product product) {
        UUID uuid = UUID.randomUUID();
        product.setId(uuid);
        productMap.put(uuid, product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(productMap.get(productId));
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }
}
