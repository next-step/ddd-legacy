package kitchenpos.order.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;

public class OrderDaoWithCollection implements OrderDao {

    private long id = 0;
    private final Map<Long, Order> entities;

    public OrderDaoWithCollection() {
        this.entities = new HashMap<>();
    }

    public OrderDaoWithCollection(List<Order> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(Order::getId,
                                                          Function.identity()));
    }

    @Override
    public Order save(Order entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        return entities.values()
                       .stream()
                       .anyMatch(ot -> orderTableId.equals(ot.getOrderTableId())
                                       && orderStatuses.contains(ot.getOrderStatus()));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return entities.values()
                       .stream()
                       .anyMatch(ot -> orderTableIds.contains(ot.getOrderTableId())
                                       && orderStatuses.contains(ot.getOrderStatus()));
    }
}
