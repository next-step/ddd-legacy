package kitchenpos.infra.order;

import java.util.*;
import kitchenpos.domain.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {

  private final Map<UUID, OrderTable> db = new HashMap<>();

  @Override
  public Optional<OrderTable> findById(UUID orderTableId) {
    return Optional.ofNullable(db.get(orderTableId));
  }

  @Override
  public OrderTable save(OrderTable orderTable) {
    return db.put(orderTable.getId(), orderTable);
  }

  @Override
  public List<OrderTable> findAll() {
    return db.values().stream().toList();
  }
}
