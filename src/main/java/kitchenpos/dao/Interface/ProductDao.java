package kitchenpos.dao.Interface;

import kitchenpos.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product save(Product entity);

    Optional<Product> findById(Long Id);

    List<Product> findAll();
}
