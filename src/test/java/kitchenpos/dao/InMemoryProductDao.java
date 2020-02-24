package kitchenpos.dao;

import kitchenpos.model.Product;

import java.util.*;

public class InMemoryProductDao implements ProductDao {

    private final Map<Long, Product> entities = new HashMap<>();

    @Override
    public Product save(Product entity) {
        entities.put(entity.getId(), entity);
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
