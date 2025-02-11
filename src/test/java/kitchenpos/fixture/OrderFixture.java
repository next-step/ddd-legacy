package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static kitchenpos.TestConstant.*;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;

public class OrderFixture {

    private OrderFixture() {
    }

    public static Order createOrder(final UUID id, final OrderType orderType, final OrderStatus orderStatus, final LocalDateTime orderDateTime,
                                    final OrderLineItem orderLineItem, final String deliveryAddress) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createOrder(final UUID id, final OrderType orderType, final OrderStatus orderStatus, final LocalDateTime orderDateTime,
                                    final OrderLineItem orderLineItem, final String deliveryAddress, OrderTable orderTable, UUID orderTableId) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }

    public static Order createOrder(OrderLineItem orderLineItem, String deliveryAddress) {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, ORDER_STATUS_주문대기,null, orderLineItem, deliveryAddress);
      }

    public static Order createOrder(OrderLineItem orderLineItem) {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, ORDER_STATUS_주문대기, null,
                orderLineItem, "강남구");
    }

    public static Order createOrder(OrderLineItem orderLineItem, OrderStatus orderStatus) {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, orderStatus, null,
                orderLineItem, "강남구");
    }

    public static Order createOrder(OrderLineItem orderLineItem, OrderType orderType, OrderStatus orderStatus, OrderTable orderTable) {
        return createOrder(ORDER_UUID, orderType, orderStatus, null,
                orderLineItem, "강남구", orderTable, orderTable.getId());
        }

    public static Order createOrder(OrderLineItem orderLineItem, OrderType orderType, OrderStatus orderStatus) {
        return createOrder(ORDER_UUID, orderType, orderStatus, null,
                orderLineItem, "강남구");
    }

    public static Order createOrder() {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구");
    }

    public static Order createOrder(OrderType orderType) {
        return createOrder(ORDER_UUID, orderType, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구");
    }


    public static Order createOrder(OrderStatus orderStatus, OrderTable orderTable) {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, orderStatus, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구", orderTable);
    }

    public static Order createOrder(UUID id, OrderType orderType, OrderStatus orderStatus, LocalDateTime orderDateTime, OrderLineItem orderLineItem, String deliveryAddress, OrderTable orderTable) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }
}
