package kitchenpos.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    List<Order> findAll();

    Optional<Order> findById(UUID orderId);

    Order save(Order order);
}
