package kitchenpos.stub;

import java.util.Collections;
import java.util.List;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

public class OrderStub {

	private static final String DEFAULT_DELIVERY_ADDRESS = "기본 주소";

	private OrderStub() {
	}

	public static Order createEatIn() {
		return createCustom(OrderType.EAT_IN, Collections.singletonList(OrderLineItemStub.createDefault()), null, OrderStatus.ACCEPTED);
	}

	public static Order createTakeout() {
		return createCustom(OrderType.TAKEOUT, Collections.singletonList(OrderLineItemStub.createDefault()), null, OrderStatus.ACCEPTED);
	}

	public static Order createDelivery() {
		return createCustom(OrderType.DELIVERY, Collections.singletonList(OrderLineItemStub.createDefault()), DEFAULT_DELIVERY_ADDRESS, OrderStatus.ACCEPTED);
	}

	public static Order createCustom(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress, OrderStatus orderStatus) {
		Order order = new Order();
		order.setType(orderType);
		order.setOrderLineItems(orderLineItems);
		order.setDeliveryAddress(deliveryAddress);
		order.setStatus(orderStatus);
		return order;
	}
}
