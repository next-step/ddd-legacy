package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
