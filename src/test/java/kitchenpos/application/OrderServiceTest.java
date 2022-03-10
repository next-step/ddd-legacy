package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.testBuilders.MenuBuilder.DEFAULT_MENU_PRICE;
import static kitchenpos.testBuilders.MenuBuilder.aDefaultMenu;
import static kitchenpos.testBuilders.OrderBuilder.*;
import static kitchenpos.testBuilders.OrderLineItemBuilder.*;
import static kitchenpos.testBuilders.OrderTableBuilder.aEmptyOrderTable;
import static kitchenpos.testBuilders.OrderTableBuilder.aNotEmptyOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private OrderTableRepository orderTableRepository;

	@Mock
	private KitchenridersClient kitchenridersClient;

	@InjectMocks
	private OrderService orderService;

	@DisplayName("주문을 생성 시 웨이팅 상태로 초기화된다")
	@Test
	void create() {
		// given
		OrderTable orderTable = aNotEmptyOrderTable().build();
		UUID orderTableId = orderTable.getId();

		OrderLineItem orderLineItemForRequest = aOrderLineItem()
				.withPrice(DEFAULT_MENU_PRICE)
				.withQuantity(1)
				.build();

		Order request = new Order();
		request.setType(OrderType.EAT_IN);
		request.setOrderTableId(orderTableId);
		request.setOrderLineItems(Collections.singletonList(orderLineItemForRequest));

		Menu menu = aDefaultOrderLineItem().build().getMenu();

		Order order = aDeliveryOrder().build();
		UUID orderId = order.getId();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(menu));
		given(menuRepository.findById(any())).willReturn(Optional.of(menu));
		given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
		given(orderRepository.save(any(Order.class))).willAnswer(returnsFirstArg());

		// when
		Order result = orderService.create(request);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.WAITING);
	}

	@DisplayName("주문 생성 시 주문타입이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void createNotExistOrderType() {
		// given
		Order request = aOrder()
				.withType(null)
				.build();

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 생성 시 주문라인아이템이 존재하지 않을 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문라인아이템: {0}")
	@NullAndEmptySource
	void createNotExistOrderLineItems(List<OrderLineItem> orderLineItems) {
		// given
		Order request = aOrder()
				.withType(OrderType.DELIVERY)
				.withOrderLineItems(orderLineItems)
				.build();

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 생성 시 주문하려는 주문라인아이템 중 존재하지 않는 메뉴가 하나라도 있는 경우 예외가 발생한다'")
	@Test
	void createNotExistMenu() {
		// given
		Order request = aOrder()
				.withType(OrderType.DELIVERY)
				.withOrderLineItems(aOrderLineItem().build())
				.build();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.emptyList());

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 생성 시 매장식사가 아니고 주문라인아이템의 수량이 0 미만일 경우 예외가 발생한다")
	@Test
	void createInvalidQuantity() {
		// given
		Order request = aOrder()
				.withType(OrderType.DELIVERY)
				.withOrderLineItems(new OrderLineItem())
				.build();

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문하려는 메뉴 중 전시중이 아닌 메뉴가 하나라도 포함된 경우 주문 생성 시 예외가 발생한다")
	@ParameterizedTest(name = "주문타입이 {0} 일 때 ")
	@EnumSource(OrderType.class)
	void createInvalidMenu(OrderType type) {
		// given
		Order request = aOrderByType(type)
				.withOrderLineItems(aOrderLineItemWithHiddenMenu().build())
				.build();

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 생성 시 입력받은 메뉴의 가격이 영속화 되어있는 메뉴 가격과 다른 경우 주문 생성 시 예외가 발생한다")
	@ParameterizedTest(name = "주문타입이 {0} 일 때 ")
	@EnumSource(OrderType.class)
	void createMismatchPrice(OrderType type) {
		BigDecimal inputMenuPrice = BigDecimal.valueOf(3000);
		BigDecimal persistenceMenuPrice = BigDecimal.valueOf(4000);

		Order request = aOrderByType(type)
				.withOrderLineItems(aOrderLineItemWithPrice(inputMenuPrice).build())
				.build();

		Menu menu = aDefaultMenu().withPrice(persistenceMenuPrice).build();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(menu));
		given(menuRepository.findById(any())).willReturn(Optional.of(menu));

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("배달 타입의 주문 생성 시 배송주소가 비어있는 경우 예외가 발생한다")
	@ParameterizedTest(name = "배송주소: {0}")
	@NullAndEmptySource
	void createEmptyDeliveryAddress(String deliveryAddress) {
		// given
		Menu menu = aDefaultOrderLineItem().build().getMenu();

		OrderLineItem orderLineItemForRequest = aOrderLineItem()
				.withMenu(menu)
				.withPrice(menu.getPrice())
				.withQuantity(1)
				.build();

		Order request = aOrder()
				.withType(OrderType.DELIVERY)
				.withOrderLineItems(orderLineItemForRequest)
				.withDeliveryAddress(deliveryAddress)
				.build();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(menu));
		given(menuRepository.findById(any())).willReturn(Optional.of(menu));

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("매장식사 타입의 주문 생성 시 해당 주문테이블이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void createNotExistOrderTable() {
		// given
		UUID notExistOrderTableId = UUID.randomUUID();

		OrderLineItem orderLineItemForRequest = aOrderLineItem()
				.withPrice(DEFAULT_MENU_PRICE)
				.withQuantity(1)
				.build();

		Order request = new Order();
		request.setType(OrderType.EAT_IN);
		request.setOrderTableId(notExistOrderTableId);
		request.setOrderLineItems(Collections.singletonList(orderLineItemForRequest));

		Menu menu = aDefaultOrderLineItem().build().getMenu();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(menu));
		given(menuRepository.findById(any())).willReturn(Optional.of(menu));
		given(orderTableRepository.findById(notExistOrderTableId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("매장식사 타입의 주문 생성 시 해당 주문테이블이 미사용중인 경우 예외가 발생한다")
	@Test
	void createNotEmptyOrderTable() {
		// given
		OrderTable orderTable = aEmptyOrderTable().build();
		UUID orderTableId = orderTable.getId();

		OrderLineItem orderLineItemForRequest = aOrderLineItem()
				.withPrice(DEFAULT_MENU_PRICE)
				.withQuantity(1)
				.build();

		Order request = new Order();
		request.setType(OrderType.EAT_IN);
		request.setOrderTableId(orderTableId);
		request.setOrderLineItems(Collections.singletonList(orderLineItemForRequest));

		Menu menu = aDefaultOrderLineItem().build().getMenu();

		given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(menu));
		given(menuRepository.findById(any())).willReturn(Optional.of(menu));
		given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

		// when then
		assertThatThrownBy(() -> orderService.create(request))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 수락 상태로 변경할 수 있다")
	@Test
	void accept() {
		// given
		Order order = aDefaultOrder().withStatus(OrderStatus.WAITING).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		Order result = orderService.accept(orderId);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.ACCEPTED);
	}

	@DisplayName("주문을 수락 상태로 변경 시 주문이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void acceptNotExistOrder() {
		// given
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.accept(orderId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문을 수락 상태로 변경 시 현재 주문이 대기 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void acceptNotWaiting(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.WAITING);

		Order order = aDefaultOrder().withStatus(orderStatus).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.accept(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 수락 상태로 변경 시 해당 주문이 배달 타입인 경우 배달클라이언트에 배달을 요청한다")
	@ParameterizedTest(name = "주문이 {0} 타입 - {1} 번 요청")
	@CsvSource(value = {
			"DELIVERY, 1",
			"EAT_IN  , 0",
			"TAKEOUT , 0"
	})
	void acceptDeliveryRequest(OrderType orderType, int times) {
		// given
		Order order = aOrderByType(orderType).withStatus(OrderStatus.WAITING).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		orderService.accept(orderId);

		// then
		verify(kitchenridersClient, times(times)).requestDelivery(any(), any(), any());
	}

	@DisplayName("주문을 조리완료 상태로 변경할 수 있다")
	@Test
	void serve() {
		// given
		Order order = aDefaultOrder().withStatus(OrderStatus.ACCEPTED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		Order result = orderService.serve(orderId);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.SERVED);
	}

	@DisplayName("주문을 조리완료 상태로 변경 시 주문이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void serveNotExistOrder() {
		// given
		Order order = aDefaultOrder().withStatus(OrderStatus.ACCEPTED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.serve(orderId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문을 조리완료 상태로 변경 시 주문이 수락 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void serveNotAccept(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.ACCEPTED);

		Order order = aDefaultOrder().withStatus(orderStatus).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.serve(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 배달중 상태로 변경할 수 있다")
	@Test
	void startDelivery() {
		// given
		Order order = aDeliveryOrder().withStatus(OrderStatus.SERVED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		Order result = orderService.startDelivery(orderId);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.DELIVERING);
	}

	@DisplayName("주문을 배달중 상태로 변경 시 주문이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void startDeliveryNotExistOrder() {
		// given
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.startDelivery(orderId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문을 배달중 상태로 변경 시 주문의 타입이 배달주문이 아닌 경우 예외가 발생한다")
	@Test
	void startDeliveryNotDelivery() {
		// given
		Order order = aTakeoutOrder().withStatus(OrderStatus.SERVED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.startDelivery(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 배달중 상태로 변경 시 주문이 조리완료 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void startDeliveryNotDelivery(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.SERVED);

		Order order = aTakeoutOrder().withStatus(OrderStatus.SERVED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.startDelivery(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 배달완료 상태로 변경할 수 있다")
	@Test
	void completeDelivery() {
		// given
		Order order = aDeliveryOrder().withStatus(OrderStatus.DELIVERING).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		Order result = orderService.completeDelivery(orderId);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.DELIVERED);
	}

	@DisplayName("주문을 배달완료 상태로 변경 시 주문이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void completeDeliveryNotExistOrder() {
		// given
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.completeDelivery(orderId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문을 배달완료 상태로 변경 시 주문이 배달중 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void completeDeliveryNotExistOrder(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.DELIVERING);

		Order order = aTakeoutOrder().withStatus(OrderStatus.SERVED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.completeDelivery(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 완료 상태로 변경할 수 있다")
	@Test
	void complete() {
		// given
		Order order = aDeliveryOrder().withStatus(OrderStatus.DELIVERED).build();
		UUID orderId = order.getId();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		Order result = orderService.complete(orderId);

		// then
		assertThat(result.getStatus()).isSameAs(OrderStatus.COMPLETED);
	}

	@DisplayName("주문을 완료 상태로 변경 시 주문이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void completeNotExistOrder() {
		// given
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderService.complete(orderId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문을 완료 상태로 변경 시 테이크아웃 타입의 주문이고 조리완료 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void completeTakeoutNotServed(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.SERVED);

		Order order = aTakeoutOrder().withStatus(orderStatus).build();
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.complete(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 완료 상태로 변경 시 매장식사 타입의 주문이고 조리완료 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void completeEatInNotServed(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.SERVED);

		Order order = aEatInOrder().withStatus(orderStatus).build();
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.complete(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 완료 상태로 변경 시 배달 타입의 주문이고 배달완료 상태가 아닌 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 상태: {0}")
	@EnumSource(OrderStatus.class)
	void completeDeliveryNotDelivered(OrderStatus orderStatus) {
		// given
		assumeFalse(orderStatus == OrderStatus.DELIVERED);

		Order order = aDeliveryOrder().withStatus(orderStatus).build();
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when then
		assertThatThrownBy(() -> orderService.complete(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문을 완료 상태로 변경 시 매장식사 타입의 주문인 경우 테이블을 미사용으로 변경한다")
	@Test
	void completeOrderTableClear() {
		// given
		Order order = aServedEatInOrder().build();
		UUID orderId = UUID.randomUUID();

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
		given(orderRepository.existsByOrderTableAndStatusNot(any(), same(OrderStatus.COMPLETED))).willReturn(false);

		// when 
		Order result = orderService.complete(orderId);

		// then
		assertAll(
				() -> assertThat(result.getOrderTable().isEmpty()).isTrue(),
				() -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero()
		);
	}

	@DisplayName("모든 주문을 조회한다")
	@Test
	void name() {
		// given
		Order takeoutOrder = aTakeoutOrder().build();
		Order deliveryOrder = aDeliveryOrder().build();

		List<Order> orders = Arrays.asList(takeoutOrder, deliveryOrder);

		given(orderService.findAll()).willReturn(orders);

		// when
		List<Order> result = orderService.findAll();

		// then
		assertThat(result).containsExactly(takeoutOrder, deliveryOrder);
	}
}
