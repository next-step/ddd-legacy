package kitchenpos.infra.persistence;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends ProductRepository, JpaRepository<Product, UUID> {

    List<Product> findAllByIdIn(List<UUID> ids);
}
