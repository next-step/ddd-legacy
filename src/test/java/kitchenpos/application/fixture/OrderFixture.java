package kitchenpos.application.fixture;

import kitchenpos.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static class Delivery {

        public static Order initializeOrder(List<OrderLineItem> orderLineItems, String deliveryAddress, LocalDateTime orderDateTime) {
            return createOrder(OrderType.DELIVERY, orderLineItems, deliveryAddress, null, null, null, orderDateTime);
        }

        public static Order createOrderByStatus(List<OrderLineItem> orderLineItems, String deliveryAddress, OrderStatus orderStatus, LocalDateTime orderDateTime) {
            return createOrder(OrderType.DELIVERY, orderLineItems, deliveryAddress, null, null, orderStatus, orderDateTime);
        }
    }

    public static class EatIn {
        public static Order initializeOrder(List<OrderLineItem> orderLineItems, OrderTable orderTable, LocalDateTime orderDateTime) {
            return createOrder(OrderType.EAT_IN, orderLineItems, null, orderTable.getId(), orderTable, null, orderDateTime);
        }

        public static Order createOrderByStatus(List<OrderLineItem> orderLineItems, OrderTable orderTable, OrderStatus orderStatus, LocalDateTime orderDateTime) {
            return createOrder(OrderType.EAT_IN, orderLineItems, null, orderTable.getId(), orderTable, orderStatus, orderDateTime);
        }
    }

    public static class TakeOut {
        public static Order initializeOrder(List<OrderLineItem> orderLineItems, LocalDateTime orderDateTime) {
            return createOrder(OrderType.TAKEOUT, orderLineItems, null, null, null, null, orderDateTime);
        }

        public static Order createOrderByStatus(List<OrderLineItem> orderLineItems, OrderStatus orderStatus, LocalDateTime orderDateTime) {
            return createOrder(OrderType.TAKEOUT, orderLineItems, null, null, null, orderStatus, orderDateTime);
        }
    }

    public static Order createOrder(OrderType type, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableId, OrderTable orderTable, OrderStatus status, LocalDateTime orderDateTime) {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(order, "type", type);
        ReflectionTestUtils.setField(order, "orderLineItems", orderLineItems);
        ReflectionTestUtils.setField(order, "deliveryAddress", deliveryAddress);
        ReflectionTestUtils.setField(order, "orderTable", orderTable);
        ReflectionTestUtils.setField(order, "orderTableId", orderTableId);
        ReflectionTestUtils.setField(order, "status", status);
        ReflectionTestUtils.setField(order, "orderDateTime", orderDateTime);

        return order;
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        ReflectionTestUtils.setField(orderLineItem, "menuId", menuId);
        ReflectionTestUtils.setField(orderLineItem, "price", price);
        ReflectionTestUtils.setField(orderLineItem, "quantity", quantity);
        return orderLineItem;
    }

    private OrderFixture() {
    }
}
