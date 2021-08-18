package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAllById(List<UUID> ids);

    Optional<Product> findById(UUID productId);

    Product save(Product product);

    List<Product> findAll();
}
