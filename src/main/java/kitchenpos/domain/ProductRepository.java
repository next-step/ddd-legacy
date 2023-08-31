package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProductRepository {
    Product save(Product entity);
    Optional<Product> findById(UUID uuid);
    List<Product> findAll();
    List<Product> findAllByIdIn(List<UUID> ids);
}
