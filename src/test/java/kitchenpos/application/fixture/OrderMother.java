package kitchenpos.application.fixture;

import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.domain.OrderType.TAKEOUT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

public class OrderMother {

	private static Map<String, CreateOrder> creators = new HashMap<>();

	static {
		creators.put("배달주문", ((orderLineItems, orderTableId) -> 주문_request(DELIVERY, orderLineItems, "", orderTableId)));
		creators.put("홀주문", ((orderLineItems, orderTableId) -> 주문_request(EAT_IN, orderLineItems, "", orderTableId)));
		creators.put("포장주문", ((orderLineItems, orderTableId) -> 주문_request(TAKEOUT, orderLineItems, "", orderTableId)));
	}

	public static CreateOrder findCreatorByName(String name) {
		return creators.get(name);
	}

	private static Order 주문_request(
		OrderType orderType,
		List<OrderLineItem> orderLineItems,
		String deliveryAddress,
		UUID orderTableId
	) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setType(orderType);
		order.setOrderLineItems(orderLineItems);
		order.setDeliveryAddress(deliveryAddress);
		order.setOrderTableId(orderTableId);

		return order;
	}

	public interface CreateOrder {

		Order create(List<OrderLineItem> orderLineItems, UUID orderTableId);
	}
}
