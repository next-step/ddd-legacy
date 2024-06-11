package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {
    @Nonnull
    Optional<OrderTable> findById(@Nonnull UUID orderTableId);

    @Nonnull
    OrderTable save(@Nonnull OrderTable orderTable);

    @Nonnull
    List<OrderTable> findAll();
}
