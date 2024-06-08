package kitchenpos.application.fake.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class InMemoryOrderTableRepository implements OrderTableRepository {
  private final Map<UUID, OrderTable> maps = new ConcurrentHashMap<>();
  @Override
  public OrderTable save(OrderTable orderTable) {
    maps.put(orderTable.getId(), orderTable);
    return orderTable;
  }

  @Override
  public Optional<OrderTable> findById(UUID id) {
    return Optional.ofNullable(maps.get(id));
  }

  @Override
  public List<OrderTable> findAll() {
    return new ArrayList<>(maps.values());
  }
}
