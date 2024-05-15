package kitchenpos.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public class ServiceTestFixture {
	private static final String VALID_MENU_GROUP_NAME = "점심특선";
	private static final BigDecimal VALID_PRODUCT_PRICE = new BigDecimal("8.99");
	private static final String VALID_MENU_NAME = "버거세트";
	private static final int VALID_MENU_PRODUCT_QUANTITY = 2;
	private static final BigDecimal VALID_MENU_PRICE = new BigDecimal("17.98");
	private static final int VALID_ORDER_LINE_ITEM_QUANTITY = 1;
	private static final String VALID_ORDER_TABLE_NAME = "1번 테이블";
	private static final String VALID_PRODUCT_NAME = "치즈 버거";

	public static MenuGroup createValidMenuGroup() {
		UUID menuGroupId = UUID.randomUUID();
		MenuGroup validMenuGroup = new MenuGroup();
		validMenuGroup.setId(menuGroupId);
		validMenuGroup.setName(VALID_MENU_GROUP_NAME);
		return validMenuGroup;
	}

	public static Product createValidProduct() {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName(VALID_PRODUCT_NAME);
		product.setPrice(VALID_PRODUCT_PRICE);
		return product;
	}

	public static MenuProduct createValidMenuProduct(int quantity) {
		Product validProduct = createValidProduct();

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(validProduct);
		menuProduct.setProductId(validProduct.getId());
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}

	public static Menu createValidMenu() {
		MenuGroup menuGroup = createValidMenuGroup();

		Menu menu = new Menu();
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuGroup(menuGroup);
		menu.setId(UUID.randomUUID());
		menu.setName(VALID_MENU_NAME);
		menu.setPrice(VALID_MENU_PRICE);
		menu.setMenuProducts(
			List.of(createValidMenuProduct(VALID_MENU_PRODUCT_QUANTITY)));
		menu.setDisplayed(true);
		return menu;
	}

	public static Menu createValidMenuWithPrice(BigDecimal price) {
		Menu validMenu = createValidMenu();
		validMenu.setPrice(price);
		return validMenu;
	}

	public static OrderLineItem createOrderLineItem(Menu menu) {
		OrderLineItem validOrderLineItem = new OrderLineItem();
		validOrderLineItem.setMenu(menu);
		validOrderLineItem.setMenuId(menu.getId());
		validOrderLineItem.setQuantity(VALID_ORDER_LINE_ITEM_QUANTITY);
		validOrderLineItem.setPrice(menu.getPrice());
		return validOrderLineItem;
	}

	public static OrderTable createValidOrderTable() {
		UUID orderTableId = UUID.randomUUID();

		OrderTable validOrderTable = new OrderTable();
		validOrderTable.setId(orderTableId);
		validOrderTable.setName(VALID_ORDER_TABLE_NAME);
		validOrderTable.setOccupied(true);
		return validOrderTable;
	}

	public static Order createValidOrder(Menu menu) {
		OrderTable validOrderTable = createValidOrderTable();

		Order validOrder = new Order();
		validOrder.setId(UUID.randomUUID());
		validOrder.setType(OrderType.EAT_IN);
		validOrder.setOrderLineItems(List.of(createOrderLineItem(menu)));
		validOrder.setOrderDateTime(LocalDateTime.now());
		validOrder.setOrderTable(validOrderTable);
		validOrder.setOrderTableId(validOrderTable.getId());
		return validOrder;
	}
}
