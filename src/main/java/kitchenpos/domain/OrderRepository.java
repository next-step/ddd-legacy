package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(final Order entity);

    Optional<Order> findById(final UUID id);

    List<Order> findAll();

    boolean existsByOrderTableAndStatusNot(final OrderTable orderTable, final OrderStatus status);
}
