package kitchenpos.dao;

import kitchenpos.model.Product;

import java.util.List;
import java.util.Optional;

public interface DefaultProductDao {
    Product save(Product entity);

    Optional<Product> findById(Long id);

    List<Product> findAll();
}
