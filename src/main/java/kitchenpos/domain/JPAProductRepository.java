package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JPAProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

}
