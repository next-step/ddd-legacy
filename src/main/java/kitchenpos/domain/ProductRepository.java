package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);

    Optional<Product> findById(UUID uuid);

    Product save(Product product);

    List<Product> findAll();
}
