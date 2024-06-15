package kitchenpos.infra.repository;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductJpaRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
