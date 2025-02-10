package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();
}

