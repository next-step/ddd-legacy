package kitchenpos.product.supports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

public class ProductDaoWithCollection implements ProductDao {

    private long id = 0;
    private final Map<Long, Product> products;

    public ProductDaoWithCollection(List<Product> entities) {
        this.products = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(Product::getId,
                                                          Function.identity()));
    }

    @Override
    public Product save(Product entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        products.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
