package kitchenpos.db;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByIdIn(List<UUID> ids);
}
