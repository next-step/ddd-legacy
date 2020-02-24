package kitchenpos.dao;

import kitchenpos.model.Product;

import java.util.*;

public class InMemoryProductDao implements ProductDao {
    private Map<Long, Product> data = new HashMap<>();

    @Override
    public Product save(Product entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(data.values());
    }
}
