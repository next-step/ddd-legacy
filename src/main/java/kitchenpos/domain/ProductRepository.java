package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    List<Product> findAllByIdIn(List<UUID> ids);

    List<Product> findAll();
}
