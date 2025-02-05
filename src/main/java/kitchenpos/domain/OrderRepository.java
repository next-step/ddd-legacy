package kitchenpos.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus orderStatus);

    List<Order> findAll();
}
