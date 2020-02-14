package kitchenpos.dao;

import kitchenpos.model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product save(final Product entity);
    Optional<Product> findById(final Long id);
    List<Product> findAll();
}
