package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
	boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
