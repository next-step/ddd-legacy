package kitchenpos.infra;


import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
}
