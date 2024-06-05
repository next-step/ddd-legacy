package kitchenpos.infra.order;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {
  private final HashMap<UUID, OrderTable> orderTables = new HashMap<>();

  @Override
  public OrderTable save(OrderTable orderTable) {
    orderTables.put(orderTable.getId(), orderTable);
    return orderTable;
  }

  @Override
  public Optional<OrderTable> findById(UUID id) {
    return Optional.ofNullable(orderTables.get(id));
  }

  @Override
  public List<OrderTable> findAll() {
    return orderTables.values()
            .stream()
            .toList();
  }
}
