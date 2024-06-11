package kitchenpos.infra;

import jakarta.annotation.Nonnull;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {
    @Override
    boolean existsByOrderTableAndStatusNot(@Nonnull OrderTable orderTable, @Nonnull OrderStatus status);
}
