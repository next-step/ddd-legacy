package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
