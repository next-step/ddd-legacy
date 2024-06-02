package kitchenpos.infra.order;

import java.util.*;
import kitchenpos.domain.*;

public class InMemoryOrderRepository implements OrderRepository {

  private final Map<UUID, Order> db = new HashMap<>();

  @Override
  public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
    final Order order = db.get(orderTable.getId());
    return order.getStatus().equals(status);
  }

  @Override
  public Order save(Order order) {
    db.put(order.getId(), order);
    return order;
  }

  @Override
  public Optional<Order> findById(UUID orderId) {
    return Optional.ofNullable(db.get(orderId));
  }

  @Override
  public List<Order> findAll() {
    return db.values().stream().toList();
  }
}
