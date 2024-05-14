package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAOrderRepository extends JpaRepository<Order, UUID>, OrderRepository {

    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
