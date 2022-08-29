package kitchenpos.stub;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemStub {

	public static final int DEFAULT_QUANTITY = 2;

	private OrderLineItemStub() {
	}

	public static OrderLineItem createDefault() {
		return createCustom(DEFAULT_QUANTITY, MenuStub.createDefault());
	}

	public static OrderLineItem createCustom(int quantity, Menu menu) {
		OrderLineItem orderLineItem = new OrderLineItem();
		orderLineItem.setMenu(menu);
		orderLineItem.setPrice(menu.getPrice());
		orderLineItem.setQuantity(quantity);
		return orderLineItem;
	}
}
