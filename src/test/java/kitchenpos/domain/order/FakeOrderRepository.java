package kitchenpos.domain.order;

import kitchenpos.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderRepository implements OrderRepository {

  private final HashMap<UUID, Order> orders = new HashMap<>();

  @Override
  public Order save(Order order) {
    orders.put(order.getId(), order);

    return order;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return Optional.ofNullable(orders.get(id));
  }

  @Override
  public List<Order> findAll() {
    return orders.values()
        .stream()
        .toList();
  }

  @Override
  public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
    return orders.values()
        .stream()
        .filter(
            order -> order.getType() == OrderType.EAT_IN
        )
        .anyMatch(order -> order.getOrderTable().getId().equals(orderTable.getId())
            && !order.getStatus().equals(status));

  }
}
