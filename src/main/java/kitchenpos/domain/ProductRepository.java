package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();
}

interface JpaProductRepositoty extends ProductRepository, JpaRepository<Product, UUID> {
}
