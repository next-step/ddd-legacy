package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;

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

	private Order validOrder;
	private Menu validMenu;
	private OrderTable validOrderTable;

	@BeforeEach
	void setUp() {
		UUID validMenuGroupId = UUID.randomUUID();
		MenuGroup validMenuGroup = new MenuGroup();
		validMenuGroup.setId(validMenuGroupId);
		validMenuGroup.setName("점심특선");

		UUID validMenuId = UUID.randomUUID();
		validMenu = new Menu();
		validMenu.setId(validMenuId);
		validMenu.setName("갈비탕");
		validMenu.setPrice(new BigDecimal("15.00"));
		validMenu.setMenuGroup(validMenuGroup);
		validMenu.setDisplayed(true);

		OrderLineItem validOrderLineItem = new OrderLineItem();
		validOrderLineItem.setMenu(validMenu);
		validOrderLineItem.setMenuId(validMenuId);
		validOrderLineItem.setQuantity(1);
		validOrderLineItem.setPrice(validMenu.getPrice());

		validOrderTable = new OrderTable();
		UUID validOrderTableId = UUID.randomUUID();
		validOrderTable.setId(validOrderTableId);
		validOrderTable.setName("1번 테이블");
		validOrderTable.setOccupied(true);

		validOrder = new Order();
		UUID validOrderId = UUID.randomUUID();
		validOrder.setId(validOrderId);
		validOrder.setType(OrderType.EAT_IN);
		validOrder.setOrderLineItems(List.of(validOrderLineItem));
		validOrder.setOrderDateTime(LocalDateTime.now());
		validOrder.setOrderTable(validOrderTable);
		validOrder.setOrderTableId(validOrderTableId);

		lenient().when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));
		lenient().when(menuRepository.findAllByIdIn(List.of(validMenuId))).thenReturn(List.of(validMenu));
		lenient().when(orderTableRepository.findById(validOrderTableId)).thenReturn(Optional.of(validOrderTable));
		lenient().when(orderRepository.findById(validOrderId)).thenReturn(Optional.of(validOrder));
	}

	@Nested
	class create {
		@Test
		@DisplayName("주문 생성 시 주문 타입이 null이면 주문 생성을 할 수 없다")
		void createOrderWithNullType() {
			// given
			validOrder.setType(null);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주문 생성 시 주문 항목이 null이거나 비어있으면 주문 생성을 할 수 없다")
		void createOrderWithNullOrEmptyLineItems(List<OrderLineItem> lineItems) {
			// given
			validOrder.setOrderLineItems(lineItems);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 항목 수와 실제 메뉴 수가 일치하지 않으면 주문 생성을 할 수 없다")
		void createOrderWithMismatchedMenusAndRequests() {
			// given
			validOrder.setOrderLineItems(Arrays.asList(new OrderLineItem(), new OrderLineItem()));
			when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(validMenu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 항목의 수량이 음수이면 주문 생성을 할 수 없다")
		void createOrderWithNegativeQuantity() {
			// given
			OrderLineItem item = new OrderLineItem();
			item.setQuantity(-1);
			validOrder.setOrderLineItems(Collections.singletonList(item));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("주문 생성 시 메뉴 ID에 해당하는 메뉴가 존재하지 않으면 주문 생성을 할 수 없다")
		void createOrderWithNonExistentMenu() {
			// given
			when(menuRepository.findById(any())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("주문 생성 시 메뉴가 표시되지 않았을 경우 주문 생성을 할 수 없다")
		void createOrderWithHiddenMenu() {
			// given
			validMenu.setDisplayed(false);

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 가격과 메뉴의 가격이 다르면 주문 생성을 할 수 없다")
		void createOrderWithMismatchedPrice() {
			// given
			validOrder.getOrderLineItems().get(0).setPrice(new BigDecimal("10.00"));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("배달 주문 생성 시 배달 주소가 null이거나 비어있으면 주문 생성을 할 수 없다")
		void createDeliveryOrderWithNullOrEmptyAddress(String address) {
			// given
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setDeliveryAddress(address);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블 ID에 해당하는 테이블이 존재하지 않으면 주문 생성을 할 수 없다")
		void createEatInOrderWithNonExistentTable() {
			// given
			validOrder.setType(OrderType.EAT_IN);
			when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블이 사용 중이 아니면 주문 생성을 할 수 없다")
		void createEatInOrderWithNonOccupiedTable() {
			// given
			validOrder.setType(OrderType.EAT_IN);
			validOrderTable.setOccupied(false);

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(validOrder);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블이 사용 중이면 주문 생성을 할 수 있다")
		void createEatInOrderSuccessfully() {
			// given
			validOrder.setType(OrderType.EAT_IN);
			validOrderTable.setOccupied(true);

			when(orderRepository.save(any(Order.class))).thenReturn(validOrder);

			// when
			Order result = orderService.create(validOrder);

			// then
			assertThat(result).isNotNull();
			assertThat(result).isEqualTo(validOrder);
			assertThat(result.getOrderTable()).isEqualTo(validOrderTable);
		}

	}

	class accept {
		@Test
		@DisplayName("주문 수락 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 수락을 할 수 없다")
		void acceptNonExistentOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.accept(orderId);
				});
		}

		@Test
		@DisplayName("주문 수락 시 주문 상태가 WAITING이 아니면 주문 수락을 할 수 없다")
		void acceptNonWaitingOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setStatus(OrderStatus.ACCEPTED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.accept(orderId);
				});
		}

		@Test
		@DisplayName("배달 주문 수락 시 주문 수락을 할 수 있다")
		void acceptDeliveryOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setStatus(OrderStatus.WAITING);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order acceptedOrder = orderService.accept(orderId);

			// then
			verify(kitchenridersClient).requestDelivery(eq(orderId), any(BigDecimal.class),
				eq(validOrder.getDeliveryAddress()));
			assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
		}
	}

	@Nested
	class serve {
		@Test
		@DisplayName("주문 서빙 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 서빙을 할 수 없다")
		void serveNonExistentOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.serve(orderId);
				});
		}

		@Test
		@DisplayName("주문 서빙 시 주문 상태가 ACCEPTED가 아니면 주문 서빙을 할 수 없다")
		void serveNonAcceptedOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setStatus(OrderStatus.WAITING);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.serve(orderId);
				});
		}

		@Test
		@DisplayName("주문 서빙 시 주문 상태가 ACCEPTED일 경우 주문 상태가 SERVED로 변경된다")
		void serveAcceptedOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setStatus(OrderStatus.ACCEPTED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order servedOrder = orderService.serve(orderId);

			// then
			assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
		}
	}

	@Nested
	class startDelivery {
		@Test
		@DisplayName("배달 시작 처리 시 주문 ID에 해당하는 주문이 존재하지 않으면 배달 시작 처리를 할 수 없다")
		void startDeliveryNonExistentOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(orderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
		@DisplayName("배달 시작 처리 시 주문 타입이 배달이 아니면 배달 시작 처리를 할 수 없다")
		void startDeliveryOnNonDeliveryOrder(OrderType orderType) {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(orderType);
			validOrder.setStatus(OrderStatus.SERVED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(orderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
		@DisplayName("배달 시작 처리 시 주문 타입이 배달이고, 주문 상태가 SERVED가 아니면 배달 시작 처리를 할 수 없다")
		void startDeliveryNotServedOrder(OrderStatus nonServedStatus) {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setStatus(nonServedStatus);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(orderId);
				});
		}

		@Test
		@DisplayName("배달 시작 처리 시 주문 상태가 SERVED일 경우 주문 상태가 DELIVERING으로 변경된다")
		void startDeliveryServedOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setStatus(OrderStatus.SERVED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order deliveringOrder = orderService.startDelivery(orderId);

			// then
			assertThat(deliveringOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
		}
	}

	@Nested
	class completeDelivery {
		@Test
		@DisplayName("배달 완료 처리 시 주문 ID에 해당하는 주문이 존재하지 않으면 배달 완료 처리를 할 수 없다")
		void completeDeliveryNonExistentOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.completeDelivery(orderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
		@DisplayName("배달 완료 처리 시 주문 상태가 DELIVERING이 아니면 배달 완료 처리를 할 수 없다")
		void completeDeliveryNotDeliveringOrder(OrderStatus orderStatus) {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setStatus(orderStatus);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.completeDelivery(orderId);
				});
		}

		@Test
		@DisplayName("배달 완료 처리 시 주문 상태가 DELIVERING일 경우 주문 상태가 DELIVERED로 변경된다")
		void completeDeliveryDeliveringOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setStatus(OrderStatus.DELIVERING);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order deliveredOrder = orderService.completeDelivery(orderId);

			// then
			assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
		}

	}

	class complete {
		@Test
		@DisplayName("주문 완료 처리 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 완료 처리를 할 수 없다")
		void completeNonExistentOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(orderId);
				});
		}

		@Test
		@DisplayName("배달 주문 완료 처리 시 주문 상태가 DELIVERED가 아니면 주문 완료 처리를 할 수 없다")
		void completeNonDeliveredOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setStatus(OrderStatus.DELIVERING);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(orderId);
				});
		}

		@Test
		@DisplayName("배달 주문 완료 처리 시 주문 상태가 DELIVERED일 경우 주문 상태가 COMPLETED로 변경된다")
		void completeDeliveredOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.DELIVERY);
			validOrder.setStatus(OrderStatus.DELIVERED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order completedOrder = orderService.complete(orderId);

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@ParameterizedTest
		@MethodSource("provideExceptionCasesForTakeOutAndEatInOrderTypes")
		@DisplayName("포장 또는 매장 식사 주문 완료 처리 시 주문 상태가 SERVED가 아니면 주문 완료 처리를 할 수 없다")
		void completeIfOrderNotServedOnCompletion(OrderType type, OrderStatus status) {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(type);
			validOrder.setStatus(status);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(orderId);
				});
		}

		static Stream<Arguments> provideExceptionCasesForTakeOutAndEatInOrderTypes() {
			return Stream.of(
				arguments(OrderType.TAKEOUT, OrderStatus.WAITING),
				arguments(OrderType.TAKEOUT, OrderStatus.ACCEPTED),
				arguments(OrderType.TAKEOUT, OrderStatus.DELIVERING),
				arguments(OrderType.TAKEOUT, OrderStatus.DELIVERED),
				arguments(OrderType.TAKEOUT, OrderStatus.COMPLETED),
				arguments(OrderType.EAT_IN, OrderStatus.WAITING),
				arguments(OrderType.EAT_IN, OrderStatus.ACCEPTED),
				arguments(OrderType.EAT_IN, OrderStatus.DELIVERING),
				arguments(OrderType.EAT_IN, OrderStatus.DELIVERED),
				arguments(OrderType.EAT_IN, OrderStatus.COMPLETED)
			);
		}

		@Test
		@DisplayName("포장 주문 완료 처리 시 주문 상태가 SERVED일 경우 주문 상태가 COMPLETED로 변경된다")
		void completeServedTakeoutOrder() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.TAKEOUT);
			validOrder.setStatus(OrderStatus.SERVED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));

			// when
			Order completedOrder = orderService.complete(orderId);

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@Test
		@DisplayName("매장 식사 주문 완료 처리 시 주문 상태가 SERVED이고 해당 주문 테이블의 주문이 미완료 상태인 경우 주문 상태가 COMPLETED로 변경된다")
		void completeServedEatInOrderWhenNotCompleted() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.EAT_IN);
			validOrder.setStatus(OrderStatus.SERVED);
			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));
			when(orderRepository.existsByOrderTableAndStatusNot(validOrderTable, OrderStatus.COMPLETED)).thenReturn(
				true);

			// when
			Order completedOrder = orderService.complete(orderId);

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@Test
		@DisplayName("매장 식사 주문 완료 처리 시 주문 상태가 SERVED이고 해당 주문 테이블의 주문이 완료 상태인 경우 테이블이 사용가능 상태로 변경된다")
		void completeServedEatInOrderWithNoActiveTable() {
			// given
			UUID orderId = UUID.randomUUID();
			validOrder.setType(OrderType.EAT_IN);
			validOrder.setStatus(OrderStatus.SERVED);

			when(orderRepository.findById(orderId)).thenReturn(Optional.of(validOrder));
			when(orderRepository.existsByOrderTableAndStatusNot(validOrderTable, OrderStatus.COMPLETED)).thenReturn(
				false);

			// when
			Order completedOrder = orderService.complete(orderId);

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
			assertThat(validOrderTable.getNumberOfGuests()).isZero();
			assertThat(validOrderTable.isOccupied()).isFalse();
		}
	}

	@Nested
	class findAll {
		@Test
		@DisplayName("주문 데이터가 비어있을 때 모든 주문을 조회하면 주문 목록을 조회할 수 없다")
		void findAllOrdersWhenEmpty() {
			// given
			when(orderRepository.findAll()).thenReturn(Collections.emptyList());

			// when
			List<Order> orders = orderService.findAll();

			// then
			assertThat(orders).isEmpty();
		}

		@Test
		@DisplayName("주문 데이터가 비어있지 않을 때 모든 주문을 조회하면 주문 목록을 조회할 수 있다")
		void findAllOrdersWhenNotEmpty() {
			// given
			when(orderRepository.findAll()).thenReturn(Collections.singletonList(validOrder));

			// when
			List<Order> orders = orderService.findAll();

			// then
			assertThat(orders).isNotEmpty();
			assertThat(orders).contains(validOrder);
		}
	}
}