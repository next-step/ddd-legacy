package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    @Nonnull
    Order save(@Nonnull Order order);

    @Nonnull
    Optional<Order> findById(@Nonnull UUID orderId);

    boolean existsByOrderTableAndStatusNot(@Nonnull OrderTable orderTable, @Nonnull OrderStatus orderStatus);

    @Nonnull
    List<Order> findAll();
}
