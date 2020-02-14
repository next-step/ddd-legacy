package kitchenpos.product.supports;

import java.util.List;
import java.util.Optional;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

public class ConstraintProductDao implements ProductDao {

    public static final RuntimeException PRODUCT_CONSTRAINT_EXCEPTION = new IllegalArgumentException() {};
    private final ProductDao delegate;

    public static ConstraintProductDao withCollection() {
        return new ConstraintProductDao(new CollectionProductDao());
    }

    public ConstraintProductDao(ProductDao delegate) {
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
