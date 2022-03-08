package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
