package kitchenpos.testBuilders;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.testBuilders.OrderLineItemBuilder.aDefaultOrderLineItem;
import static kitchenpos.testBuilders.OrderTableBuilder.aEmptyOrderTable;
import static kitchenpos.testBuilders.OrderTableBuilder.aNotEmptyOrderTable;

public class OrderBuilder {
	public static final UUID DEFAULT_ORDER_ID = UUID.randomUUID();
	public static final OrderStatus DEFAULT_STATUS = OrderStatus.WAITING;
	public static final LocalDateTime DEFAULT_ORDER_DATE_TIME = LocalDateTime.now();
	public static final String DEFAULT_DELIVERY_ADDRESS = "서울특별시 잠실 시그니엘 9999층";
	private UUID id;
	private OrderType type;
	private OrderStatus status;
	private LocalDateTime orderDateTime;
	private List<OrderLineItem> orderLineItems;
	private String deliveryAddress;
	private OrderTable orderTable;
	private UUID orderTableId;

	private OrderBuilder() {
	}

	public static OrderBuilder aOrder() {
		return new OrderBuilder();
	}

	public static OrderBuilder aDefaultOrder() {
		return aTakeoutOrder();
	}
	
	public static OrderBuilder aTakeoutOrder() {
		return aOrder()
				.withType(OrderType.TAKEOUT)
				.withId(DEFAULT_ORDER_ID)
				.withStatus(DEFAULT_STATUS)
				.withOrderDateTime(DEFAULT_ORDER_DATE_TIME)
				.withOrderLineItems(aDefaultOrderLineItem().build());
	}

	public static OrderBuilder aDeliveryOrder() {
		return aOrder()
				.withType(OrderType.DELIVERY)
				.withId(DEFAULT_ORDER_ID)
				.withStatus(DEFAULT_STATUS)
				.withOrderDateTime(DEFAULT_ORDER_DATE_TIME)
				.withOrderLineItems(aDefaultOrderLineItem().build())
				.withDeliveryAddress(DEFAULT_DELIVERY_ADDRESS);
	}

	public static OrderBuilder aEatInOrder() {
		return aOrder()
				.withType(OrderType.EAT_IN)
				.withId(DEFAULT_ORDER_ID)
				.withStatus(DEFAULT_STATUS)
				.withOrderDateTime(DEFAULT_ORDER_DATE_TIME)
				.withOrderLineItems(aDefaultOrderLineItem().build())
				.withOrderTable(aEmptyOrderTable().build());
	}

	public static OrderBuilder aOrderByType(OrderType type) {
		if (type == OrderType.DELIVERY) return aDeliveryOrder();
		if (type == OrderType.TAKEOUT) return aTakeoutOrder();
		if (type == OrderType.EAT_IN) return aEatInOrder();
		throw new IllegalArgumentException("해당 타입의 주문 생성 불가 type:" + type);
	}

	public static OrderBuilder aServedEatInOrder() {
		return aEatInOrder()
				.withStatus(OrderStatus.SERVED)
				.withOrderTable(aNotEmptyOrderTable().build());
	}

	public OrderBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	public OrderBuilder withType(OrderType type) {
		this.type = type;
		return this;
	}

	public OrderBuilder withStatus(OrderStatus status) {
		this.status = status;
		return this;
	}

	public OrderBuilder withOrderDateTime(LocalDateTime orderDateTime) {
		this.orderDateTime = orderDateTime;
		return this;
	}

	public OrderBuilder withOrderLineItems(List<OrderLineItem> orderLineItems) {
		this.orderLineItems = orderLineItems;
		return this;
	}

	public OrderBuilder withOrderLineItems(OrderLineItem orderLineItem) {
		return withOrderLineItems(Collections.singletonList(orderLineItem));
	}

	public OrderBuilder withDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
		return this;
	}

	public OrderBuilder withOrderTable(OrderTable orderTable) {
		this.orderTable = orderTable;
		return this;
	}

	public OrderBuilder withOrderTableId(UUID orderTableId) {
		this.orderTableId = orderTableId;
		return this;
	}

	public Order build() {
		Order order = new Order();
		order.setId(id);
		order.setType(type);
		order.setStatus(status);
		order.setOrderDateTime(orderDateTime);
		order.setOrderLineItems(orderLineItems);
		order.setDeliveryAddress(deliveryAddress);
		order.setOrderTable(orderTable);
		order.setOrderTableId(orderTableId);
		return order;
	}
}
