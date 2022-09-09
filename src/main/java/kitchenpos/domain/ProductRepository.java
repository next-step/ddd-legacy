package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);

    List<Product> findAll();
}
