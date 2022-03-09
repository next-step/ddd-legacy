package kitchenpos.testBuilders;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.testBuilders.MenuBuilder.aDefaultMenu;

public class OrderLineItemBuilder {
	public static final Long DEFAULT_SEQ = 1L;
	public static final Menu DEFAULT_MENU = aDefaultMenu().build();
	public static final Long DEFAULT_QUANTITY = 1L;
	private Long seq;
	private Menu menu;
	private long quantity;
	private UUID menuId;
	private BigDecimal price;

	private OrderLineItemBuilder() {
	}

	public static OrderLineItemBuilder aOrderLineItem() {
		return new OrderLineItemBuilder();
	}

	public static OrderLineItemBuilder aDefaultOrderLineItem() {
		return aOrderLineItem()
				.withSeq(DEFAULT_SEQ)
				.withMenu(DEFAULT_MENU)
				.withQuantity(DEFAULT_QUANTITY);
	}

	public static OrderLineItemBuilder aOrderLineItemWithHiddenMenu() {
		return aDefaultOrderLineItem()
				.withMenu(aDefaultMenu().withDisplayed(false).build());
	}

	public static OrderLineItemBuilder aOrderLineItemWithPrice(BigDecimal price) {
		return aDefaultOrderLineItem()
				.withMenu(aDefaultMenu().withDisplayed(false).build())
				.withPrice(price);
	}

	public OrderLineItemBuilder withSeq(Long seq) {
		this.seq = seq;
		return this;
	}

	public OrderLineItemBuilder withMenu(Menu menu) {
		this.menu = menu;
		return this;
	}

	public OrderLineItemBuilder withQuantity(long quantity) {
		this.quantity = quantity;
		return this;
	}

	public OrderLineItemBuilder withMenuId(UUID menuId) {
		this.menuId = menuId;
		return this;
	}

	public OrderLineItemBuilder withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public OrderLineItem build() {
		OrderLineItem orderLineItem = new OrderLineItem();
		orderLineItem.setSeq(seq);
		orderLineItem.setMenu(menu);
		orderLineItem.setQuantity(quantity);
		orderLineItem.setMenuId(menuId);
		orderLineItem.setPrice(price);
		return orderLineItem;
	}
}
