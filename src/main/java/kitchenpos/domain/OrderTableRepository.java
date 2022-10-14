package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {

    OrderTable save(OrderTable orderTable);

    List<OrderTable> findAll();

    Optional<OrderTable> findById(UUID id);
}
