package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order entity);
    Optional<Order> findById(UUID uuid);
    List<Order> findAll();
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
