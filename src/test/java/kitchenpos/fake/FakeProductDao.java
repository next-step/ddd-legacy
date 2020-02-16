package kitchenpos.fake;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

import java.util.*;

public class FakeProductDao implements ProductDao {
    private Map<Long, Product> entities = new HashMap<>();

    @Override
    public Product save(Product entity) {
        entities.put(entity.getId(), entity);
        return null;
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
