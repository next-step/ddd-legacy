package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Optional<Product> findById(final UUID id);

    List<Product> findAllByIdIn(final List<UUID> ids);

    List<Product> findAll();

    Product save(final Product entity);
}
