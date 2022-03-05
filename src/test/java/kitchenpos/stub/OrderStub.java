package kitchenpos.stub;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.Collections;

import static kitchenpos.stub.OrderLineItemStub.*;
import static kitchenpos.stub.OrderTableStub.generateNotEmptyForTwinOrderTable;

public class OrderStub {

    public static final String HOME_DELIVERY_ADDRESS = "집주소";

    private OrderStub() {
    }

    public static Order generateTenThousandPriceDeliverTypeOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        return order;
    }

    public static Order generateTenThousandPriceEatInTypeOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        return order;
    }

    public static Order generateNegativeQuantityOrderLineItemDeliveryTypeOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(generateNegativeQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        return order;
    }

    public static Order generateEmptyOrderTypeOrderRequest() {
        Order order = new Order();
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));;
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        return order;
    }

    public static Order generateEmptyOrderLineItemDeliveryOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        return order;
    }

    public static Order generateContainingInvisibleMenuDeliveryTypeOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(generateInvisibleMenuOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        return order;
    }

    public static Order generateDifferentPriceBetweenOrderLineItemAndMenuOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(generateDifferentPriceThanMenuOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        return order;
    }

    public static Order generateEmptyDeliveryAddressDeliveryTypeOrderRequest() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        return order;
    }

    public static Order generateTenThousandPriceDeliverTypeWaitingOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceDeliverTypeAcceptedOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceDeliverTypeServedOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceDeliverTypeDeliveringOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceDeliverTypeDeliveredOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setDeliveryAddress(HOME_DELIVERY_ADDRESS);
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceTakeOutTypeServedOrder() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        return order;
    }

    public static Order generateTenThousandPriceEatInTypeServedOrder() {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderLineItems(Collections.singletonList(generateFiveThousandPriceMenuTwoQuantityOrderLineItem()));
        order.setOrderDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        order.setOrderTable(generateNotEmptyForTwinOrderTable());
        return order;
    }


}
