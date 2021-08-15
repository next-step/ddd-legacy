package kitchenpos.mock;

import kitchenpos.domain.*;

import java.util.*;

public class MockOrderRepository implements OrderRepository {
    private final OrderTableRepository orderTableRepository;
    private final Map<UUID, Order> orderMap = new HashMap<>();

    public MockOrderRepository(final OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    @Override
    public Order save(final Order order) {
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(final UUID orderId) {
        return Optional.ofNullable(orderMap.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(final OrderTable orderTable, final OrderStatus orderStatus) {
        return orderMap.values()
                .stream()
                .filter(order -> !Objects.equals(orderStatus, order.getStatus()))
                .anyMatch(order -> orderTableRepository.findById(order.getOrderTableId()).isPresent());
    }
}
