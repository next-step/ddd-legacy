package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {

    List<OrderTable> findAll();

    OrderTable save(OrderTable entity);

    Optional<OrderTable> findById(UUID uuid);
}
