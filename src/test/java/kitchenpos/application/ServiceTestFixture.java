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
	static UUID VALID_MENU_GROUP_ID;
	static MenuGroup VALID_MENU_GROUP;
	static UUID VALID_PRODUCT_ID;
	static Product VALID_PRODUCT;
	static MenuProduct VALID_MENU_PRODUCT;
	static UUID VALID_MENU_ID;
	static Menu VALID_MENU;
	static OrderLineItem VALID_ORDER_LINE_ITEM;
	static UUID VALID_ORDER_TABLE_ID;
	static OrderTable VALID_ORDER_TABLE;
	static UUID VALID_ORDER_ID;
	static Order VALID_ORDER;
	private static final String VALID_PRODUCT_NAME = "치즈 버거";

	public static void initializeMenuAndProducts() {
		VALID_MENU_GROUP_ID = UUID.randomUUID();
		VALID_MENU_GROUP = createValidMenuGroup(VALID_MENU_GROUP_ID);

		VALID_PRODUCT_ID = UUID.randomUUID();
		VALID_PRODUCT = createValidProduct(VALID_PRODUCT_ID);

		VALID_MENU_PRODUCT = createValidMenuProduct(VALID_PRODUCT, VALID_MENU_PRODUCT_QUANTITY);

		VALID_MENU_ID = UUID.randomUUID();
		VALID_MENU = createValidMenu(VALID_MENU_GROUP_ID, VALID_MENU_GROUP, VALID_MENU_ID);
	}

	public static void initializeMenuAndOrder() {
		initializeMenuAndProducts();

		VALID_ORDER_LINE_ITEM = createOrderLineItem(VALID_MENU, VALID_MENU_ID);

		VALID_ORDER_TABLE_ID = UUID.randomUUID();
		VALID_ORDER_TABLE = createValidOrderTable(VALID_ORDER_TABLE_ID);

		VALID_ORDER_ID = UUID.randomUUID();
		VALID_ORDER = createValidOrder(VALID_ORDER_LINE_ITEM, VALID_ORDER_TABLE, VALID_ORDER_TABLE_ID, VALID_ORDER_ID);
	}

	private static MenuGroup createValidMenuGroup(UUID menuGroupId) {
		MenuGroup validMenuGroup = new MenuGroup();
		validMenuGroup.setId(menuGroupId);
		validMenuGroup.setName(VALID_MENU_GROUP_NAME);
		return validMenuGroup;
	}

	private static Product createValidProduct(UUID productId) {
		Product product = new Product();
		product.setId(productId);
		product.setName(VALID_PRODUCT_NAME);
		product.setPrice(VALID_PRODUCT_PRICE);
		return product;
	}

	static MenuProduct createValidMenuProduct(Product product, int quantity) {
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(product);
		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}

	static Menu createValidMenu(UUID menuGroupId, MenuGroup menuGroup, UUID menuId) {
		VALID_MENU = new Menu();
		VALID_MENU.setMenuGroupId(menuGroupId);
		VALID_MENU.setMenuGroup(menuGroup);
		VALID_MENU.setId(menuId);
		VALID_MENU.setName(VALID_MENU_NAME);
		VALID_MENU.setPrice(VALID_MENU_PRICE);
		VALID_MENU.setMenuProducts(List.of(VALID_MENU_PRODUCT));
		VALID_MENU.setDisplayed(true);
		return VALID_MENU;
	}

	public static Menu createMenuWithPrice(BigDecimal price) {
		Menu menu = new Menu();
		menu.setId(VALID_MENU_ID);
		menu.setName(VALID_MENU.getName());
		menu.setPrice(price);
		menu.setMenuGroup(VALID_MENU_GROUP);
		menu.setMenuGroupId(VALID_MENU_GROUP_ID);
		menu.setMenuProducts(List.of(VALID_MENU_PRODUCT));
		menu.setDisplayed(VALID_MENU.isDisplayed());
		return menu;
	}

	static OrderLineItem createOrderLineItem(Menu validMenu, UUID menuId) {
		OrderLineItem validOrderLineItem = new OrderLineItem();
		validOrderLineItem.setMenu(validMenu);
		validOrderLineItem.setMenuId(menuId);
		validOrderLineItem.setQuantity(VALID_ORDER_LINE_ITEM_QUANTITY);
		validOrderLineItem.setPrice(validMenu.getPrice());
		return validOrderLineItem;
	}

	static OrderTable createValidOrderTable(UUID orderTableId) {
		OrderTable validOrderTable = new OrderTable();
		validOrderTable.setId(orderTableId);
		validOrderTable.setName(VALID_ORDER_TABLE_NAME);
		validOrderTable.setOccupied(true);
		return validOrderTable;
	}

	static Order createValidOrder(
		OrderLineItem validOrderLineItem,
		OrderTable validOrderTable,
		UUID orderTableId,
		UUID validOrderId) {
		Order validOrder = new Order();
		validOrder.setId(validOrderId);
		validOrder.setType(OrderType.EAT_IN);
		validOrder.setOrderLineItems(List.of(validOrderLineItem));
		validOrder.setOrderDateTime(LocalDateTime.now());
		validOrder.setOrderTable(validOrderTable);
		validOrder.setOrderTableId(orderTableId);
		return validOrder;
	}
}
