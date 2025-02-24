package kitchenpos.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    List<Product> findAll();

    List<Product> findAllByIdIn(List<UUID> list);
}

