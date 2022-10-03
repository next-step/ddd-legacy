package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemMother {

	public static OrderLineItem create(UUID menuId, BigDecimal menuPrice, int quantity) {
		OrderLineItem orderLineItem = new OrderLineItem();

		orderLineItem.setMenuId(menuId);
		orderLineItem.setQuantity(quantity);
		orderLineItem.setPrice(menuPrice);

		return orderLineItem;
	}
}
