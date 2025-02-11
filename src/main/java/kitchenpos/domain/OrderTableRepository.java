package kitchenpos.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderTableRepository {
    Optional<OrderTable> findById(UUID orderTableId);

    OrderTable save(OrderTable orderTable);

    List<OrderTable> findAll();
}
