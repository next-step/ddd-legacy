package kitchenpos.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.db.ProductJpaRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return productJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public Product save(Product entity) {
        return productJpaRepository.save(entity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }
}
