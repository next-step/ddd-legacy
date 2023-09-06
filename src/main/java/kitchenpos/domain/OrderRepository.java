package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order orderTable);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
