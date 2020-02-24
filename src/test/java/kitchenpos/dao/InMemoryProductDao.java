package kitchenpos.dao;

import kitchenpos.model.Product;
import kitchenpos.support.ProductBuilder;

import java.util.*;

public class InMemoryProductDao implements ProductDao {

    private final Map<Long, Product> entities = new HashMap<>();

    @Override
    public Product save(Product entity) {
        Product savedProduct = new ProductBuilder()
            .id(entity.getId())
            .name(entity.getName())
            .price(entity.getPrice())
            .build();

        entities.put(entity.getId(), savedProduct);
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(entities.values());
    }
}
