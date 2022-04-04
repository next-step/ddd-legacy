package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID orderId);
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    Order save(Order order);

    List<Order> findAll();
}
