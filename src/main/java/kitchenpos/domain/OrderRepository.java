package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository  {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    Optional<Order> findById(UUID orderId);

    Order save(Order order);

    List<Order> findAll();
}

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {}