package kitchenpos.domain;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order, UUID> {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    List<Order> findAll();
}
