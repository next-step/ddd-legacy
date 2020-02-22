package kitchenpos.bo.mock;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestProductDao implements ProductDao {
    private static final Map<Long, Product> data = new HashMap<>();

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
        return data.values()
                .stream()
                .collect(Collectors.toList());
    }
}
