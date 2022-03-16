package kitchenpos.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);

    Optional<Product> findById(UUID productId);

    Product save(Product product);

    List<Product> findAll();
}

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {

}
