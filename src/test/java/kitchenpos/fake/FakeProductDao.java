package kitchenpos.fake;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

import java.util.*;

public class FakeProductDao implements ProductDao {
    private final Map<Long, Product> values = new HashMap<>();

    @Override
    public Product save(Product entity) {
        values.put(entity.getId(), entity);
        return null;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(values.values());
    }
}
