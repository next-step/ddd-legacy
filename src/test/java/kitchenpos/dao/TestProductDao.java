package kitchenpos.dao;

import kitchenpos.model.Product;

import java.util.*;

public class TestProductDao implements ProductDao {

    private final Map<Long, Product> products = new HashMap();

    @Override
    public Product save(Product entity) {
        products.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList(products.values());
    }
}
