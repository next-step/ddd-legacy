package kitchenpos.application.fake.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class InMemoryOrderRepository implements OrderRepository {

  private final Map<UUID, Order> maps = new ConcurrentHashMap<>();

  @Override
  public Order save(Order order) {
    maps.put(order.getId(), order);
    return order;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return Optional.ofNullable(maps.get(id));
  }

  @Override
  public List<Order> findAll() {
    return new ArrayList<>(maps.values());
  }

  @Override
  public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
    return maps.values()
        .stream()
        .anyMatch(order -> Objects.nonNull(order.getOrderTable()) && order.getOrderTable().getId()
            .equals(orderTable.getId()) && !order.getStatus().equals(status));
  }
}
