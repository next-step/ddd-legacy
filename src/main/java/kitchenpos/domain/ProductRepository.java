package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    List<Product> findAllById(List<UUID> ids);

    Optional<Product> findById(UUID id);
}
