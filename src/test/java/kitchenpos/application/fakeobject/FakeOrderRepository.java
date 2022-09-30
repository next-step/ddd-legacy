package kitchenpos.application.fakeobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderRepository implements OrderRepository {
    private List<Order> orderList = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public FakeOrderRepository() {
        for (int i = 1; i <= 5; i++) {
            Order order = new Order();
            order.setId(UUID.fromString("191fa247-b5f3-4b51-b175-e65db523f71" + i));
            orderList.add(order);
        }
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus completed) {
        for (Order order : orderList) {
            if (order.getOrderTableId().equals(orderTable.getId()) && !order.getStatus().equals(completed)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
            for (Order orderItem : orderList) {
                if (orderItem.getId().equals(order.getId())) {
                    try {
                        orderItem = objectMapper.readValue(objectMapper.writeValueAsString(order), Order.class);
                        return orderItem;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        order.setId(UUID.randomUUID());
        orderList.add(order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        for (Order order : orderList) {
            if (order.getId().equals(orderId)) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return orderList;
    }

    public void setOrderTablesOnOrder(List<OrderTable> orderTableList) {
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            if (order.getOrderTableId() == null) {
                OrderTable orderTable = orderTableList.get(orderTableList.size() % (i + 1));
                order.setOrderTableId(orderTable.getId());
            }
        }
    }
}
