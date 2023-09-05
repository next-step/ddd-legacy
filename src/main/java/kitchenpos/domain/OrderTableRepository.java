package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {

    OrderTable save(final OrderTable entity);

    Optional<OrderTable> findById(final UUID id);

    List<OrderTable> findAll();

}
