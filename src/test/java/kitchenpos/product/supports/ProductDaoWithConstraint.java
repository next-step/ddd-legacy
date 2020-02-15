package kitchenpos.product.supports;

import java.util.List;
import java.util.Optional;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

public class ProductDaoWithConstraint implements ProductDao {

    public static final IllegalArgumentException PRODUCT_CONSTRAINT_EXCEPTION = new IllegalArgumentException() {};
    private final ProductDao delegate;

    public static ProductDaoWithConstraint withCollection(List<Product> entities) {
        return new ProductDaoWithConstraint(new ProductDaoWithCollection(entities));
    }

    public ProductDaoWithConstraint(ProductDao delegate) {
        this.delegate = delegate;
    }

    @Override
    public Product save(Product entity) {
        if (entity.getName() == null) {
            throw PRODUCT_CONSTRAINT_EXCEPTION;
        }
        return delegate.save(entity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return delegate.findAll();
    }
}
