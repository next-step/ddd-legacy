package kitchenpos.domain;

import java.util.*;

public interface OrderTableRepository {
    OrderTable save(OrderTable orderTable);

    Optional<OrderTable> findById(UUID orderTableId);

    List<OrderTable> findAll();
}
