package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {

}
