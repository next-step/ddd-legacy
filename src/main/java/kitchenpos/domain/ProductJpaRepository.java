package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, UUID>, ProductRepository {

    List<Product> findAllByIdIn(List<UUID> ids);
}
