package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kitchenpos.common.MockitoUnitTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.stub.MenuStub;
import kitchenpos.stub.OrderLineItemStub;
import kitchenpos.stub.OrderStub;
import kitchenpos.stub.OrderTableStub;

class OrderServiceTest extends MockitoUnitTest {

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

	private Menu menu;
	private Order eatInOrder;
	private Order takeoutOrder;
	private Order deliveryOrder;

	@BeforeEach
	void setUp() {
		menu = MenuStub.createDefault();
		eatInOrder = OrderStub.createEatIn();
		takeoutOrder = OrderStub.createTakeout();
		deliveryOrder = OrderStub.createDelivery();
	}

	@DisplayName("주문 생성 시")
	@Nested
	class CreateTest {

		@DisplayName("식당 내 식사 주문을 할 수 있다.")
		@Test
		void createEatIn() {
			// given
			OrderTable orderTable = OrderTableStub.createCustom("주문 테이블", true, 1);

			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(menu));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(orderTable));

			when(orderRepository.save(any()))
				.thenReturn(eatInOrder);

			// when
			Order result = orderService.create(eatInOrder);

			// then
			verify(orderRepository, times(1))
				.save(any());

			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getType()).isEqualTo(OrderType.EAT_IN)
			);
		}

		@DisplayName("배달 주문을 할 수 있다.")
		@Test
		void createDelivery() {
			// given
			Menu menu = MenuStub.createDefault();

			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(menu));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			when(orderRepository.save(any()))
				.thenReturn(deliveryOrder);

			// when
			Order result = orderService.create(deliveryOrder);

			// then
			verify(orderRepository, times(1))
				.save(any());

			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getType()).isEqualTo(OrderType.DELIVERY)
			);
		}

		@DisplayName("포장 주문을 할 수 있다.")
		@Test
		void createTakeout() {
			// given
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(menu));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			when(orderRepository.save(any()))
				.thenReturn(takeoutOrder);

			// when
			Order result = orderService.create(takeoutOrder);

			// then
			verify(orderRepository, times(1))
				.save(any());

			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT)
			);
		}

		@DisplayName("주문 유형이 빈 값이라면 예외 처리한다.")
		@Test
		void createFailByOrderType() {
			// given
			OrderType orderType = null;

			// when
			Order newOrder = OrderStub.createCustom(
				orderType,
				Collections.singletonList(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.WAITING
			);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> orderService.create(newOrder));
		}

		@DisplayName("주문 메뉴가 빈 값이라면 예외 처리한다.")
		@Test
		void createFailByMenu() {
			// given
			List<OrderLineItem> orderLineItems = Collections.emptyList();

			// when
			Order newOrder = OrderStub.createCustom(
				OrderType.EAT_IN,
				orderLineItems,
				null,
				OrderStatus.ACCEPTED
			);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> orderService.create(newOrder));
		}

		@DisplayName("주문 메뉴는 존재하는 메뉴여야 한다.")
		@Test
		void createFailByNonExistsMenu() {
			// given
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.emptyList());

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> orderService.create(eatInOrder));
		}

		@DisplayName("식당 내 식사가 아닌 주문 생성 시 주문 메뉴의 개수가 0개 미만이면 예외 발생")
		@Test
		void createFailByQuantity() {
			// given
			OrderLineItem orderLineItem = OrderLineItemStub.createCustom(-1, menu);

			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(orderLineItem),
				"",
				OrderStatus.WAITING
			);

			// when
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(menu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> orderService.create(order));
		}

		@DisplayName("주문 메뉴가 진열되지 않은 메뉴라면 예외 처리한다.")
		@Test
		void createFailByNonDisplayedMenu() {
			// given
			Menu nonDisplayedMenu = MenuStub.createNonDisplayed();
			Order order = OrderStub.createDelivery();

			// when
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(nonDisplayedMenu));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(nonDisplayedMenu));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.create(order));
		}

		@DisplayName("주문 생성 시 입력한 주문 메뉴의 가격이 실제 메뉴 가격과 다르면 예외 발생")
		@Test
		void createFailByNotMatchedPrice() {
			// given
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(new Menu()));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> orderService.create(eatInOrder));
		}

		@DisplayName("배달 주문 시 배달 주소가 비어있다면 예외 처리한다.")
		@Test
		void createFailByEmptyDeliveryAddress() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.WAITING
			);

			// when
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(new Menu()));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(new Menu()));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.create(order));
		}

		@DisplayName("식당 내 식사 주문은 테이블 정보를 잘못 입력하면 예외 발생")
		@Test
		void createFailByInvalidTableNumber() {
			// given
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(new Menu()));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.empty());

			// when
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> orderService.create(eatInOrder));
		}

		@DisplayName("식당 내 식사는 테이블에 착석 후 가능하다.")
		@Test
		void createFailByEmptyTable() {
			// given
			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(new Menu()));

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(OrderTableStub.createDefault()));

			// when
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.create(eatInOrder));
		}
	}

	@DisplayName("배달 시작 시")
	@Nested
	class DeliveryTest {

		@DisplayName("주문 유형이 배달이며 주문 상태가 제공 완료인 주문만 배달을 시작할 수 있다.")
		@Test
		void startDelivery() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				OrderStatus.SERVED
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			Order result = orderService.startDelivery(UUID.randomUUID());

			// then
			assertThat(result.getStatus())
				.isEqualTo(OrderStatus.DELIVERING);
		}

		@DisplayName("배달 주문이 아니면 배달 시작 상태로 변경 불가능")
		@ParameterizedTest
		@EnumSource(value = OrderType.class, names = { "TAKEOUT", "EAT_IN" })
		void startDeliveryFail01(OrderType type) {
			// given
			Order order = OrderStub.createCustom(
				type,
				List.of(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.SERVED
			);

			// when
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.startDelivery(order.getId()));
		}

		@DisplayName("주문 수행 상태가 아니면 배달 시작 상태로 변경 불가능")
		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, names = { "WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED" })
		void startDeliveryFail02(OrderStatus status) {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				status
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.startDelivery(order.getId()));
		}

		@DisplayName("배달중인 주문이 아니면 배달 완료로 변경 불가능")
		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, names = { "WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED" })
		void completeDeliveryFail(OrderStatus status) {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				status
			);

			// when
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()));
		}

		@DisplayName("주문 상태가 배달 중인 주문만 주문 완료할 수 있다.")
		@Test
		void completeDelivery() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				OrderStatus.DELIVERING
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			Order result = orderService.completeDelivery(order.getId());

			// then
			assertThat(result.getStatus())
				.isEqualTo(OrderStatus.DELIVERED);
		}
	}

	@DisplayName("주문 완료 시")
	@Nested
	class CompleteTest {

		@DisplayName("배달 완료된 배달 주문을 주문 완료 처리할 수 있다.")
		@Test
		void completeWithDelivery() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				OrderStatus.DELIVERED
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			Order result = orderService.complete(order.getId());

			// then
			assertThat(result.getStatus())
				.isEqualTo(OrderStatus.COMPLETED);
		}

		@DisplayName("식당 내 식사 주문이 완료되었을 때 테이블 상태가 비어있지 않다면 빈 상태로 변경")
		@Test
		void completeWithEatIn() {
			// given
			OrderTable orderTable = OrderTableStub.createCustom("테이블 이름", true, 5);
			Order order = OrderStub.createCustom(
				OrderType.EAT_IN,
				List.of(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.SERVED
			);
			order.setOrderTable(orderTable);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED)))
				.thenReturn(false);

			// when
			Order result = orderService.complete(order.getId());

			// then
			assertAll(
				() -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
				() -> assertThat(result.getOrderTable()).isNotNull(),
				() -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero()
			);
		}

		@DisplayName("배달 완료 주문이 아닌 경우 예외 처리한다.")
		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, names = { "WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED" })
		void completeDeliveryFailByOrderStatus(OrderStatus status) {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				status
			);

			// when
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.complete(order.getId()));
		}

		@DisplayName("포장 주문 또는 식당 내 식사 주문일 때 제공 완료 상태가 아닌 경우 예외 처리한다.")
		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, names = { "WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED" })
		void completeFailByOrderStatus(OrderStatus status) {
			// given
			Order order = OrderStub.createCustom(
				OrderType.TAKEOUT,
				Collections.singletonList(OrderLineItemStub.createDefault()),
				"",
				status
			);

			// when
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.complete(order.getId()));
		}
	}

	@DisplayName("주문 수락 시")
	@Nested
	class AcceptTest {

		@DisplayName("배달 주문을 수락하면 배달대행사에 배달 요청 후 주문 상태 변경")
		@Test
		void accept() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.DELIVERY,
				List.of(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.WAITING
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			Order result = orderService.accept(deliveryOrder.getId());

			// then
			verify(kitchenridersClient, times(1))
				.requestDelivery(any(), any(), any());

			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
			);
		}

		@DisplayName("배달이 아니면 배달대행사에 배달 요청을 하지 않고 주문 상태만 변경")
		@ParameterizedTest
		@EnumSource(value = OrderType.class, names = { "EAT_IN", "TAKEOUT" })
		void accept(OrderType orderType) {
			// given
			Order order = OrderStub.createCustom(
				orderType,
				List.of(OrderLineItemStub.createDefault()),
				"",
				OrderStatus.WAITING
			);

			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// when
			Order result = orderService.accept(order.getId());

			// then
			verify(kitchenridersClient, times(0))
				.requestDelivery(any(), any(), any());

			assertThat(result.getStatus())
				.isNotEqualTo(OrderStatus.DELIVERING);
		}

		@Test
		@DisplayName("주문 수락 상태로 변경 시 주문 ID가 잘못되어 있으면 오류")
		void acceptFailByInvalidId() {
			// given
			when(orderRepository.findById(any()))
				.thenReturn(Optional.empty());

			// when
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> orderService.accept(UUID.randomUUID()));
		}
	}

	@DisplayName("주문 제공 시")
	@Nested
	class ServeTest {

		@Test
		@DisplayName("수락 상태의 주문은 제공 상태로 변경 가능하다.")
		void serve() {
			// given
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(eatInOrder));

			// when
			Order result = orderService.serve(eatInOrder.getId());

			// then
			assertThat(result.getStatus())
				.isEqualTo(OrderStatus.SERVED);
		}

		@DisplayName("수락 상태가 아닌 주문을 수행하면 오류 발생")
		@Test
		void serveFailByNotAccepted() {
			// given
			Order order = OrderStub.createCustom(
				OrderType.EAT_IN,
				List.of(OrderLineItemStub.createDefault()),
				"주소",
				OrderStatus.WAITING
			);

			// when
			when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderService.serve(order.getId()));
		}
	}
}
