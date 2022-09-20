package kitchenpos.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class InMemoryOrderTableRepository implements OrderTableRepository {

  private final Map<UUID, OrderTable> store = new HashMap<>();

  @Override
  public OrderTable save(OrderTable orderTable) {
    store.put(orderTable.getId(), orderTable);
    return orderTable;
  }

  @Override
  public Optional<OrderTable> findById(UUID orderTableId) {
    return Optional.ofNullable(store.get(orderTableId));
  }

  @Override
  public List<OrderTable> findAll() {
    return new ArrayList<>(store.values());
  }
}
