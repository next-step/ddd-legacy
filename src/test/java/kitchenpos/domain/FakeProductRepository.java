package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeProductRepository implements ProductRepository {

    private final HashMap<UUID, Product> inMemory = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(inMemory::get)
                .toList();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(inMemory.get(id));
    }

    @Override
    public Product save(Product product) {
        inMemory.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(inMemory.values());
    }
}
