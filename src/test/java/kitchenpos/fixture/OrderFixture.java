package kitchenpos.fixture;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {
	public static Order WAITING_DELIVERY(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.WAITING);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("강남구");
		return order;
	}

	public static Order ACCEPTED_TAKEOUT(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.TAKEOUT);
		order.setStatus(OrderStatus.ACCEPTED);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		return order;
	}

	public static Order SERVED_DELIVERY(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("강남구");
		return order;
	}

	public static Order DELIVERING_DELIVERY(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.DELIVERING);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("강남구");
		return order;
	}

	public static Order SERVED_TAKEOUT(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.TAKEOUT);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		return order;
	}

	public static Order SERVED_EAT_IN(Menu menu, OrderTable orderTable) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.EAT_IN);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2021, Month.AUGUST, 7, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setOrderTable(orderTable);
		return order;
	}

	private static OrderLineItem orderLineItem(Menu menu) {
		OrderLineItem orderLineItem = new OrderLineItem();
		orderLineItem.setMenu(menu);
		orderLineItem.setQuantity(3);
		return orderLineItem;
	}
}
