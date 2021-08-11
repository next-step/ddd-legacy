package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(final Product product);

    Optional<Product> findById(final UUID productId);

    List<Product> findAll();

    List<Product> findAllById(List<UUID> productIds);
}
