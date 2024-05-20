package kitchenpos.fixture;

import kitchenpos.domain.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;

public class OrderFixture {


    public static @NotNull Order createOrder(List<OrderLineItem> input) {
        return createOrder(OrderType.TAKEOUT, null, null, input);
    }

    public static @NotNull Order createOrder(OrderType orderType, Menu... menu) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (Menu m : menu) {
            OrderLineItem orderLineItem = createOrderLineItem(m);
            orderLineItems.add(orderLineItem);
        }
        return createOrder(orderType, null, null, orderLineItems);
    }

    public static @NotNull Order createEatInOrder(OrderTable orderTable, Menu... menu) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (Menu m : menu) {
            OrderLineItem orderLineItem = createOrderLineItem(m);
            orderLineItems.add(orderLineItem);
        }
        return createOrder(OrderType.EAT_IN, orderTable, null, orderLineItems);
    }

    public static @NotNull Order createEatInOrder(OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(orderType, orderTable, null, orderLineItems);
    }

    public static @NotNull Order createDeliveryOrder(String deliveryAddress, Menu... menu) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (Menu m : menu) {
            OrderLineItem orderLineItem = createOrderLineItem(m);
            orderLineItems.add(orderLineItem);
        }
        return createOrder(OrderType.DELIVERY, null, deliveryAddress, orderLineItems);
    }
    public static @NotNull Order createDeliveryOrder(OrderType orderType, String deliveryAddress, List<OrderLineItem> orderLineItems) {
        return createOrder(orderType, null, deliveryAddress, orderLineItems);
    }

    public static @NotNull Order createOrder(OrderType orderType, OrderTable orderTable, String deliveryAddress, List<OrderLineItem> orderLineItems) {
        final var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        if (orderType == OrderType.DELIVERY) {
            order.setDeliveryAddress(deliveryAddress);
        }
        if ((orderType == OrderType.EAT_IN) && orderTable != null) {
            order.setOrderTableId(orderTable.getId());
            order.setOrderTable(orderTable);
        }
        order.setOrderLineItems(orderLineItems);
        return order;
    }

}
