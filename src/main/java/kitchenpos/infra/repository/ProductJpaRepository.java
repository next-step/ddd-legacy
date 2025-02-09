package kitchenpos.infra.repository;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository extends ProductRepository, JpaRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
