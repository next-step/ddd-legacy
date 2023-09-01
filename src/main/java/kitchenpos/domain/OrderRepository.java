package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
}

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
