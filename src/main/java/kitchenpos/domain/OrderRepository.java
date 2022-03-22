package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    Optional<Order> findById(UUID orderId);

    List<Order> findAll();

    Order save(Order order);
}
