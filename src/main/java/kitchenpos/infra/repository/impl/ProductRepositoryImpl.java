package kitchenpos.infra.repository.impl;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.repository.ProductJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return productJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }
}

