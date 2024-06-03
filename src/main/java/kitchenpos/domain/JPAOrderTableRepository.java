package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAOrderTableRepository extends JpaRepository<OrderTable, UUID>,
        OrderTableRepository {

}
