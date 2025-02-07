package kitchenpos.mock.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> dataMap = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        UUID id = product.getId();
        dataMap.put(id, product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return dataMap.values()
            .stream()
            .filter(product -> product.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return dataMap.values().stream()
            .filter(product -> ids.contains(product.getId()))
            .toList();
    }
}
