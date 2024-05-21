package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

    @Override
    List<Product> findAllByIdIn(List<UUID> ids);
}
