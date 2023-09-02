package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);
    Product save(Product product);

    Optional<Product> findById(UUID id);
    List<Product> findAll();
}

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
