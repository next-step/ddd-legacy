package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceIntegrationTest extends IntegrationTest {
	private static final BigDecimal MENU_PRICE = new BigDecimal(20000);
	private static final BigDecimal PRODUCT_PRICE = new BigDecimal(15000);
	private static final BigDecimal ORDER_LINE_ITEM_PRICE = new BigDecimal(20000);
	private static final BigDecimal ORDER_LINE_ITEM_PRICE_NOT_EQUAL_TO_MENU = new BigDecimal(100);
	private static final long ORDER_LINE_ITEM_QUANTITY = 3;
	private static final long ORDER_LINE_ITEM_QUANTITY_NEGATIVE = -1;

	@Autowired
	private OrderService orderService;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MenuGroupRepository menuGroupRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderTableRepository orderTableRepository;
	@Autowired
	private OrderRepository orderRepository;

	private Product givenProduct;
	private MenuGroup givenMenuGroup;

	@BeforeEach
	void setUp() {
		givenProduct = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
	}

	@DisplayName("주문")
	@Test
	void order() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		menuGroupRepository.findAll();
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.SAT_ORDER_TABLE());
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.EAT_IN);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
		newOrder.setOrderTableId(orderTable.getId());

		// when
		Order actualOrder = orderService.create(newOrder);

		// then
		assertAll(
			() -> assertThat(actualOrder.getId()).isNotNull(),
			() -> assertThat(actualOrder.getType()).isEqualTo(newOrder.getType()),
			() -> assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
			() -> assertThat(actualOrder.getOrderTable().getId()).isEqualTo(newOrder.getOrderTableId()),
			() -> {
				List<OrderLineItem> actualOrderLineItems = actualOrder.getOrderLineItems();
				OrderLineItem actualOrderLineItem = actualOrderLineItems.get(0);
				assertAll(
					() -> assertThat(actualOrderLineItem.getSeq()).isNotNull(),
					() -> assertThat(actualOrderLineItem.getMenu().getId()).isEqualTo(orderLineItem.getMenuId()),
					() -> assertThat(actualOrderLineItem.getQuantity()).isEqualTo(orderLineItem.getQuantity())
				);
			}
		);
	}

	@DisplayName("종류없으면 주문 실패")
	@Test
	void failOrderWhenNullCategory() {
		// given
		Order order = new Order();
		order.setType(null); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문항목 없으면 주문 실패")
	@ParameterizedTest
	@NullAndEmptySource
	void failOrderWhenOrderItemNullOreEmpty(List<OrderLineItem> orderLineItems) {
		// given
		Order order = new Order();
		order.setType(OrderType.EAT_IN);
		order.setOrderLineItems(orderLineItems); // null or empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("매장식사 아니면서 주문 항목 수량이 0보다 작으면 주문 실패")
	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
	void failOrderWhenOrderItemIsNegativeNotHere(OrderType orderType) {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY_NEGATIVE);

		Order newOrder = new Order();
		newOrder.setType(orderType);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 항목에 해당하는 메뉴가 전시상태가아니면 주문 실패")
	@Test
	void failOrderWhenMenuIsNotDisplay() {
		// given
		Menu menu = menuRepository.save(MenuFixture.HIDDEN_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.EAT_IN);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 항목의 가격과 해당하는 메뉴의 가격이 같지 않으면 주문 실패")
	@Test
	void failOrderWhenPriceIsNotEqual() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE_NOT_EQUAL_TO_MENU, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.EAT_IN);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("배달 주소가 빈 값이면 주문 실패")
	@ParameterizedTest
	@NullAndEmptySource
	void failOrderWhenDeliveryIsNull(String deliveryAddress) {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.DELIVERY);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
		newOrder.setDeliveryAddress(deliveryAddress); // null or empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("매장식사이면서 주문테이블 없으면 주문 실패")
	@Test
	void failOrderWhenOrderTableIsNull() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.EAT_IN);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
		newOrder.setOrderTableId(UUID.randomUUID()); // unknown

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("매장식사이면서 주문테이블이 비어 있으면 주문 실패")
	@Test
	void failOrderWhenOrderTableIsEmpty() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
		OrderLineItem orderLineItem = OrderFixture.orderLineItemRequest(menu, ORDER_LINE_ITEM_PRICE, ORDER_LINE_ITEM_QUANTITY);

		Order newOrder = new Order();
		newOrder.setType(OrderType.EAT_IN);
		newOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
		newOrder.setOrderTableId(orderTable.getId());

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(newOrder);

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 수락")
	@Test
	void acceptOrder() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.waitingDeliveryOrder(menu));

		// when
		Order actualOrder = orderService.accept(order.getId());

		// then
		assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
	}

	@DisplayName("대기중이 아니면 주문 수락 실패")
	@Test
	void failAcceptingOrderWhenIsNotWaiting() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.acceptTakeOutOrder(menu)); // not waiting

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.accept(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 서빙")
	@Test
	void servOrder() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.acceptTakeOutOrder(menu));

		// when
		Order actualOrder = orderService.serve(order.getId());

		// then
		assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
	}

	@DisplayName("수락됨이 아니면 주문 서빙 실패")
	@Test
	void failServingOrderWhenNotAccept() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.waitingDeliveryOrder(menu));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.serve(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 배달")
	@Test
	void deliveryOrder() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.serveDeliveryOrder(menu));

		// when
		Order actualOrder = orderService.startDelivery(order.getId());

		// then
		assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
	}

	@DisplayName("주문 종류가 배달이 아니면 주문 배달 실패")
	@Test
	void failDeliveringOrderWhenIsNotDeliveryType() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.serveTakeoutOrder(menu)); // not delivery

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.startDelivery(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 상태가 서빙됨이 아니면 주문 배달 실패")
	@Test
	void failDeliveringOrderWhenIsNotServing() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.waitingDeliveryOrder(menu));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.startDelivery(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 배달 완료")
	@Test
	void completeDelivery() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.deliveryOrder(menu));

		// when
		Order actualOrder = orderService.completeDelivery(order.getId());

		// then
		assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
	}

	@DisplayName("주문 종류가 배달이 아니면 주문 배달 완료 실패")
	@Test
	void failCompletingDeliveryWhenIsNotDeliveryType() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.acceptTakeOutOrder(menu)); // not delivery

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.completeDelivery(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 상태가 배달중이 아니면 주문 배달 완료 실패")
	@Test
	void failCompletingDeliveryWhenIsNotDelivery() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.serveDeliveryOrder(menu)); // not delivering

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.completeDelivery(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 완료")
	@Test
	void completeOrder() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.serveEatInOrder(menu, orderTable));

		// when
		Order actualOrder = orderService.complete(order.getId());
		OrderTable actualOrderTable = orderTableRepository.findById(orderTable.getId()).get();

		// then
		assertAll(
			() -> assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED),
			() -> assertThat(actualOrderTable.getNumberOfGuests()).isZero(),
			() -> assertThat(actualOrderTable.isEmpty()).isTrue()
		);
	}

	@DisplayName("주문 종류가 배달이면서 주문 상태가 배달됨이 아니면 주문 완료 실패")
	@Test
	void failCompletingOrderWhenIsDeliveryOrderIsNotCompleteOrder() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.serveDeliveryOrder(menu)); // not delivered

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.complete(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 종류가 테이크아웃이거나 매장에서 식사이면서 주문 상태가 서빙됨이 아니면 주문 완료 실패")
	@Test
	void failCompletingOrderWhenIsNotDeliveryOrderIsNotCompleteServe() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.acceptTakeOutOrder(menu));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.complete(order.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 주문 조회")
	@Test
	void readOrderList() {
		// given
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, givenMenuGroup, givenProduct));
		Order order = orderRepository.save(OrderFixture.acceptTakeOutOrder(menu));

		// when
		List<Order> orders = orderService.findAll();

		// then
		List<UUID> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

		assertAll(
			() -> assertThat(orders).isNotEmpty(),
			() -> assertThat(ids).contains(order.getId())
		);
	}
}
