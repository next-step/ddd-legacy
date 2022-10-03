package kitchenpos.application.fakeobject;

import kitchenpos.domain.*;

import java.util.*;

public class FakeOrderRepository implements OrderRepository {
    private Map<UUID, Order> orderMap = new HashMap<>();

    public FakeOrderRepository() {
        for (int i = 1; i <= 5; i++) {
            Order order = new Order();
            UUID id = UUID.fromString("191fa247-b5f3-4b51-b175-e65db523f71" + i);
            order.setId(id);
            orderMap.put(id, order);
        }
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus completed) {
        for (Order order : orderMap.values()) {
            if (orderTable.getId().equals(order.getOrderTableId()) && !completed.equals(order.getStatus())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Order save(Order order) {
        if (order.getId() != null && orderMap.containsKey(order.getId())) {
            orderMap.put(order.getId(), order);
            return order;
        }
        order.setId(UUID.randomUUID());
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        if (orderMap.containsKey(orderId)) {
            return Optional.of(orderMap.get(orderId));
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    public void setOrderTablesOnOrder(List<OrderTable> orderTableList) {
        for (int i = 0; i < orderMap.values().size(); i++) {
            List<Order> orderList = new ArrayList<>(orderMap.values());
            Order order = orderList.get(i);
            if (order.getOrderTableId() == null) {
                OrderTable orderTable = orderTableList.get(orderTableList.size() % (i + 1));
                order.setOrderTableId(orderTable.getId());
            }
        }
    }
}
