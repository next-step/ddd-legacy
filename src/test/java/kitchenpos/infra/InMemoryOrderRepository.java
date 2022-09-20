package kitchenpos.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class InMemoryOrderRepository implements OrderRepository {

  private final Map<UUID, Order> store = new HashMap<>();

  @Override
  public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
    return store.values().stream()
        .filter(order -> order.getOrderTable().getId().equals(orderTable.getId()))
        .map(order -> !order.getStatus().equals(status))
        .findFirst()
        .orElse(false);
  }

  @Override
  public Order save(Order order) {
    store.put(order.getId(), order);
    return order;
  }

  @Override
  public Optional<Order> findById(UUID orderId) {
    return Optional.ofNullable(store.get(orderId));
  }

  @Override
  public List<Order> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public void deleteAll() {
    store.clear();
  }
}
