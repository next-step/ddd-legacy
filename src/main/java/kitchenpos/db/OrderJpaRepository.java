package kitchenpos.db;

import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
