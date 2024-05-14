package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {
    @Override
    boolean existsByOrderTableAndStatusNot(@Nonnull OrderTable orderTable, @Nonnull OrderStatus status);
}
