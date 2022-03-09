package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

public class OrderFixture {
	public static Order waitingDeliveryOrder(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.WAITING);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("서울");
		return order;
	}

	public static Order acceptTakeOutOrder(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.TAKEOUT);
		order.setStatus(OrderStatus.ACCEPTED);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		return order;
	}

	public static Order serveDeliveryOrder(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("서울");
		return order;
	}

	public static Order deliveryOrder(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.DELIVERY);
		order.setStatus(OrderStatus.DELIVERING);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setDeliveryAddress("서울");
		return order;
	}

	public static Order serveTakeoutOrder(Menu menu) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.TAKEOUT);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		return order;
	}

	public static Order serveEatInOrder(Menu menu, OrderTable orderTable) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(OrderType.EAT_IN);
		order.setStatus(OrderStatus.SERVED);
		order.setOrderDateTime(LocalDateTime.of(2022, Month.MARCH, 9, 0, 0, 0, 0));
		order.setOrderLineItems(Collections.singletonList(orderLineItem(menu)));
		order.setOrderTable(orderTable);
		return order;
	}

	public static OrderLineItem orderLineItemRequest(Menu menu, BigDecimal price, long quantity) {
		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(menu.getId());
		orderLineItemRequest.setPrice(price);
		orderLineItemRequest.setQuantity(quantity);
		return orderLineItemRequest;
	}

	private static OrderLineItem orderLineItem(Menu menu) {
		OrderLineItem orderLineItem = new OrderLineItem();
		orderLineItem.setMenu(menu);
		orderLineItem.setQuantity(3);
		return orderLineItem;
	}
}
