package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
}
