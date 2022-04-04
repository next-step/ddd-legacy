package kitchenpos.repository;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JapOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {
}
