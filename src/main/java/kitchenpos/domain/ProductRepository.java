package kitchenpos.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findAllByIdIn(List<UUID> ids);
}
