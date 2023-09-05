package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findAllByIdIn(List<UUID> ids);
}

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findAllByIdIn(List<UUID> ids);
}
