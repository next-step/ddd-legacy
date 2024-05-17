package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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

import fixture.OrderFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
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

	@Nested
	class create {
		@Test
		@DisplayName("주문 생성 시 주문 타입이 null이면 주문 생성을 할 수 없다")
		void createOrderWithNullType() {
			// given
			Order nullTypedOrder = OrderFixture.createValid();
			nullTypedOrder.setType(null);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(nullTypedOrder);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주문 생성 시 주문 항목이 null이거나 비어있으면 주문 생성을 할 수 없다")
		void createOrderWithNullOrEmptyLineItems(List<OrderLineItem> lineItems) {
			// given
			Order orderWithNullOrEmptyOrderLineItems = OrderFixture.createValid();
			orderWithNullOrEmptyOrderLineItems.setOrderLineItems(lineItems);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNullOrEmptyOrderLineItems);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 항목 수와 실제 메뉴 수가 일치하지 않으면 주문 생성을 할 수 없다")
		void createOrderWithMismatchedMenusAndRequests() {
			// given
			Order orderWithNotMatchedOrderLineItems = OrderFixture.createValid();
			OrderLineItem orderLineItem = orderWithNotMatchedOrderLineItems.getOrderLineItems().getFirst();

			List<OrderLineItem> orderLineItems = List.of(orderLineItem, new OrderLineItem());
			orderWithNotMatchedOrderLineItems.setOrderLineItems(orderLineItems);

			when(menuRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(orderLineItem.getMenu()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNotMatchedOrderLineItems);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 항목의 수량이 음수이면 주문 생성을 할 수 없다")
		void createOrderWithNegativeQuantity() {
			// given
			Order orderWithInvalidOrderLineItem = OrderFixture.createValid();
			OrderLineItem orderLineItemWithNegativeQuantity = orderWithInvalidOrderLineItem.getOrderLineItems()
				.getFirst();
			orderLineItemWithNegativeQuantity.setQuantity(-1);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithInvalidOrderLineItem);
				});
		}

		@Test
		@DisplayName("주문 생성 시 메뉴 ID에 해당하는 메뉴가 존재하지 않으면 주문 생성을 할 수 없다")
		void createOrderWithNonExistentMenu() {
			// given
			Order orderWithNonExistentMenu = OrderFixture.createValid();
			Menu nonExistentMenu = orderWithNonExistentMenu.getOrderLineItems().getFirst().getMenu();

			when(menuRepository.findAllByIdIn(List.of(nonExistentMenu.getId()))).thenReturn(
				Collections.singletonList(nonExistentMenu));
			when(menuRepository.findById(nonExistentMenu.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNonExistentMenu);
				});
		}

		@Test
		@DisplayName("주문 생성 시 메뉴가 표시되지 않았을 경우 주문 생성을 할 수 없다")
		void createOrderWithHiddenMenu() {
			// given
			Order orderWithNotDisplayedMenu = OrderFixture.createValid();
			Menu notDisplayedMenu = orderWithNotDisplayedMenu.getOrderLineItems().getFirst().getMenu();
			notDisplayedMenu.setDisplayed(false);

			when(menuRepository.findAllByIdIn(List.of(notDisplayedMenu.getId())))
				.thenReturn(Collections.singletonList(notDisplayedMenu));

			when(menuRepository.findById(notDisplayedMenu.getId())).thenReturn(Optional.of(notDisplayedMenu));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNotDisplayedMenu);
				});
		}

		@Test
		@DisplayName("주문 생성 시 요청된 가격과 메뉴의 가격이 다르면 주문 생성을 할 수 없다")
		void createOrderWithMismatchedPrice() {
			// given
			Order orderWithMismatchedPrice = OrderFixture.createValid();
			Menu validMenu = orderWithMismatchedPrice.getOrderLineItems().getFirst().getMenu();
			orderWithMismatchedPrice.getOrderLineItems().getFirst().setPrice(new BigDecimal("10.00"));

			when(menuRepository.findAllByIdIn(List.of(validMenu.getId())))
				.thenReturn(Collections.singletonList(validMenu));

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithMismatchedPrice);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("배달 주문 생성 시 배달 주소가 null이거나 비어있으면 주문 생성을 할 수 없다")
		void createDeliveryOrderWithNullOrEmptyAddress(String address) {
			// given
			Order orderWithInvalidDeliveryAddress = OrderFixture.createValid();
			orderWithInvalidDeliveryAddress.setType(OrderType.DELIVERY);
			orderWithInvalidDeliveryAddress.setDeliveryAddress(address);

			Menu validMenu = orderWithInvalidDeliveryAddress.getOrderLineItems().getFirst().getMenu();

			when(menuRepository.findAllByIdIn(List.of(validMenu.getId())))
				.thenReturn(Collections.singletonList(validMenu));

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithInvalidDeliveryAddress);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블 ID에 해당하는 테이블이 존재하지 않으면 주문 생성을 할 수 없다")
		void createEatInOrderWithNonExistentTable() {
			// given
			Order orderWithNonExistentOrderTable = OrderFixture.createValid();
			Menu validMenu = orderWithNonExistentOrderTable.getOrderLineItems().getFirst().getMenu();

			when(menuRepository.findAllByIdIn(List.of(validMenu.getId())))
				.thenReturn(Collections.singletonList(validMenu));

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));

			orderWithNonExistentOrderTable.setType(OrderType.EAT_IN);
			when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNonExistentOrderTable);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블이 사용 중이 아니면 주문 생성을 할 수 없다")
		void createEatInOrderWithNonOccupiedTable() {
			// given
			Order orderWithNonExistentOrderTable = OrderFixture.createValid();
			orderWithNonExistentOrderTable.setType(OrderType.EAT_IN);
			orderWithNonExistentOrderTable.getOrderTable().setOccupied(false);

			Menu validMenu = orderWithNonExistentOrderTable.getOrderLineItems().getFirst().getMenu();

			when(menuRepository.findAllByIdIn(List.of(validMenu.getId())))
				.thenReturn(Collections.singletonList(validMenu));

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));

			when(orderTableRepository.findById(orderWithNonExistentOrderTable.getOrderTableId())).thenReturn(
				Optional.of(orderWithNonExistentOrderTable.getOrderTable()));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.create(orderWithNonExistentOrderTable);
				});
		}

		@Test
		@DisplayName("매장 내 식사 주문 생성 시 주문 테이블이 사용 중이면 주문 생성을 할 수 있다")
		void createEatInOrderSuccessfully() {
			// given
			Order orderWithOccupiedOrderTable = OrderFixture.createValid();
			orderWithOccupiedOrderTable.setType(OrderType.EAT_IN);
			orderWithOccupiedOrderTable.getOrderTable().setOccupied(true);

			Menu validMenu = orderWithOccupiedOrderTable.getOrderLineItems().getFirst().getMenu();

			when(menuRepository.findAllByIdIn(List.of(validMenu.getId())))
				.thenReturn(Collections.singletonList(validMenu));

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));

			when(orderTableRepository.findById(orderWithOccupiedOrderTable.getOrderTableId())).thenReturn(
				Optional.of(orderWithOccupiedOrderTable.getOrderTable()));

			when(orderRepository.save(any(Order.class))).thenReturn(orderWithOccupiedOrderTable);

			// when
			Order result = orderService.create(orderWithOccupiedOrderTable);

			// then
			assertThat(result).isNotNull();
			assertThat(result).isEqualTo(orderWithOccupiedOrderTable);
			assertThat(result.getOrderTable()).isEqualTo(orderWithOccupiedOrderTable.getOrderTable());
		}

	}

	@Nested
	class accept {
		@Test
		@DisplayName("주문 수락 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 수락을 할 수 없다")
		void acceptNonExistentOrder() {
			// given
			UUID nonExistentOrderId = UUID.randomUUID();

			when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.accept(nonExistentOrderId);
				});
		}

		@Test
		@DisplayName("주문 수락 시 주문 상태가 WAITING이 아니면 주문 수락을 할 수 없다")
		void acceptNonWaitingOrder() {
			// given
			Order acceptedOrder = OrderFixture.createValid();
			acceptedOrder.setStatus(OrderStatus.ACCEPTED);
			when(orderRepository.findById(acceptedOrder.getId())).thenReturn(Optional.of(acceptedOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.accept(acceptedOrder.getId());
				});
		}

		@Test
		@DisplayName("배달 주문 수락 시 주문 수락을 할 수 있다")
		void acceptDeliveryOrder() {
			// given
			Order deliveryOrder = OrderFixture.createValid();
			deliveryOrder.setType(OrderType.DELIVERY);
			deliveryOrder.setStatus(OrderStatus.WAITING);
			when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.of(deliveryOrder));

			// when
			Order acceptedOrder = orderService.accept(deliveryOrder.getId());

			// then
			verify(kitchenridersClient).requestDelivery(eq(deliveryOrder.getId()), any(BigDecimal.class),
				eq(deliveryOrder.getDeliveryAddress()));
			assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
		}
	}

	@Nested
	class serve {
		@Test
		@DisplayName("주문 서빙 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 서빙을 할 수 없다")
		void serveNonExistentOrder() {
			// given
			UUID nonExistentOrderId = UUID.randomUUID();
			when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.serve(nonExistentOrderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"ACCEPTED"})
		@DisplayName("주문 서빙 시 주문 상태가 ACCEPTED가 아니면 주문 서빙을 할 수 없다")
		void serveNonAcceptedOrder(OrderStatus orderStatus) {
			// given
			Order nonAcceptedOrder = OrderFixture.createValid();
			nonAcceptedOrder.setStatus(orderStatus);

			when(orderRepository.findById(nonAcceptedOrder.getId())).thenReturn(Optional.of(nonAcceptedOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.serve(nonAcceptedOrder.getId());
				});
		}

		@Test
		@DisplayName("주문 서빙 시 주문 상태가 ACCEPTED일 경우 주문 상태가 SERVED로 변경된다")
		void serveAcceptedOrder() {
			// given
			Order acceptedOrder = OrderFixture.createValid();
			acceptedOrder.setStatus(OrderStatus.ACCEPTED);

			when(orderRepository.findById(acceptedOrder.getId())).thenReturn(Optional.of(acceptedOrder));

			// when
			Order servedOrder = orderService.serve(acceptedOrder.getId());

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
			UUID nonExistentOrderId = UUID.randomUUID();
			when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(nonExistentOrderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERY"})
		@DisplayName("배달 시작 처리 시 주문 타입이 배달이 아니면 배달 시작 처리를 할 수 없다")
		void startDeliveryOnNonDeliveryOrder(OrderType nonDeliveryStatus) {
			// given
			Order nonDeliveryOrder = OrderFixture.createValidWithTypeAndStatus(nonDeliveryStatus, OrderStatus.SERVED);

			when(orderRepository.findById(nonDeliveryOrder.getId())).thenReturn(Optional.of(nonDeliveryOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(nonDeliveryOrder.getId());
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
		@DisplayName("배달 시작 처리 시 주문 타입이 배달이고, 주문 상태가 SERVED가 아니면 배달 시작 처리를 할 수 없다")
		void startDeliveryNonServedOrder(OrderStatus nonServedStatus) {
			// given
			Order nonServedDeliveryOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY,
				nonServedStatus);

			when(orderRepository.findById(nonServedDeliveryOrder.getId()))
				.thenReturn(Optional.of(nonServedDeliveryOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.startDelivery(nonServedDeliveryOrder.getId());
				});
		}

		@Test
		@DisplayName("배달 시작 처리 시 주문 상태가 SERVED일 경우 주문 상태가 DELIVERING으로 변경된다")
		void startDeliveryServedOrder() {
			// given
			Order servedDeliveryOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY,
				OrderStatus.SERVED);

			when(orderRepository.findById(servedDeliveryOrder.getId())).thenReturn(Optional.of(servedDeliveryOrder));

			// when
			Order deliveringOrder = orderService.startDelivery(servedDeliveryOrder.getId());

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
			UUID nonExistentOrderId = UUID.randomUUID();

			when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.completeDelivery(nonExistentOrderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERING"})
		@DisplayName("배달 완료 처리 시 주문 상태가 DELIVERING이 아니면 배달 완료 처리를 할 수 없다")
		void completeDeliveryNonDeliveringOrder(OrderStatus orderStatus) {
			// given
			Order nonDeliveringOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY, orderStatus);

			when(orderRepository.findById(nonDeliveringOrder.getId())).thenReturn(Optional.of(nonDeliveringOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.completeDelivery(nonDeliveringOrder.getId());
				});
		}

		@Test
		@DisplayName("배달 완료 처리 시 주문 상태가 DELIVERING일 경우 주문 상태가 DELIVERED로 변경된다")
		void completeDeliveryDeliveringOrder() {
			// given
			Order deliveringOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY,
				OrderStatus.DELIVERING);

			when(orderRepository.findById(deliveringOrder.getId())).thenReturn(Optional.of(deliveringOrder));

			// when
			Order deliveredOrder = orderService.completeDelivery(deliveringOrder.getId());

			// then
			assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
		}

	}

	@Nested
	class complete {
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
		@DisplayName("주문 완료 처리 시 주문 ID에 해당하는 주문이 존재하지 않으면 주문 완료 처리를 할 수 없다")
		void completeNonExistentOrder() {
			// given
			UUID nonExistentOrderId = UUID.randomUUID();

			when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(nonExistentOrderId);
				});
		}

		@ParameterizedTest
		@EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERED"})
		@DisplayName("배달 주문 완료 처리 시 주문 상태가 DELIVERED가 아니면 주문 완료 처리를 할 수 없다")
		void completeNonDeliveredOrder(OrderStatus nonDeliveredStatus) {
			// given
			Order nonDeliveredOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY, nonDeliveredStatus);

			when(orderRepository.findById(nonDeliveredOrder.getId())).thenReturn(Optional.of(nonDeliveredOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(nonDeliveredOrder.getId());
				});
		}

		@Test
		@DisplayName("배달 주문 완료 처리 시 주문 상태가 DELIVERED일 경우 주문 상태가 COMPLETED로 변경된다")
		void completeDeliveredOrder() {
			// given
			Order deliveredOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.DELIVERY, OrderStatus.DELIVERED);

			when(orderRepository.findById(deliveredOrder.getId())).thenReturn(Optional.of(deliveredOrder));

			// when
			Order completedOrder = orderService.complete(deliveredOrder.getId());

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@ParameterizedTest
		@MethodSource("provideExceptionCasesForTakeOutAndEatInOrderTypes")
		@DisplayName("포장 또는 매장 식사 주문 완료 처리 시 주문 상태가 SERVED가 아니면 주문 완료 처리를 할 수 없다")
		void completeIfOrderNotServedOnCompletion(OrderType type, OrderStatus status) {
			// given
			Order invalidOrder = OrderFixture.createValidWithTypeAndStatus(type, status);

			when(orderRepository.findById(invalidOrder.getId())).thenReturn(Optional.of(invalidOrder));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderService.complete(invalidOrder.getId());
				});
		}

		@Test
		@DisplayName("포장 주문 완료 처리 시 주문 상태가 SERVED일 경우 주문 상태가 COMPLETED로 변경된다")
		void completeServedTakeoutOrder() {
			// given
			Order servedTakeoutOrder = OrderFixture.createValidWithTypeAndStatus(OrderType.TAKEOUT, OrderStatus.SERVED);

			when(orderRepository.findById(servedTakeoutOrder.getId())).thenReturn(Optional.of(servedTakeoutOrder));

			// when
			Order completedOrder = orderService.complete(servedTakeoutOrder.getId());

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@Test
		@DisplayName("매장 식사 주문 완료 처리 시 주문 상태가 SERVED이고 해당 주문 테이블의 주문이 미완료 상태인 경우 주문 상태가 COMPLETED로 변경된다")
		void completeServedEatInOrderWhenNotCompleted() {
			// given
			Order servedEatInOrderWithCompletedOrderTable = OrderFixture.createValidWithTypeAndStatus(OrderType.EAT_IN,
				OrderStatus.SERVED);

			when(orderRepository.findById(servedEatInOrderWithCompletedOrderTable.getId())).thenReturn(
				Optional.of(servedEatInOrderWithCompletedOrderTable));
			when(
				orderRepository.existsByOrderTableAndStatusNot(servedEatInOrderWithCompletedOrderTable.getOrderTable(),
					OrderStatus.COMPLETED)
			).thenReturn(true);

			// when
			Order completedOrder = orderService.complete(servedEatInOrderWithCompletedOrderTable.getId());

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		}

		@Test
		@DisplayName("매장 식사 주문 완료 처리 시 주문 상태가 SERVED이고 해당 주문 테이블의 주문이 완료 상태인 경우 테이블이 사용가능 상태로 변경된다")
		void completeServedEatInOrderWithNoActiveTable() {
			// given
			Order servedEatInOrder = OrderFixture.createValidWithTypeAndStatus(
				OrderType.EAT_IN, OrderStatus.SERVED);

			when(orderRepository.findById(servedEatInOrder.getId()))
				.thenReturn(Optional.of(servedEatInOrder));

			when(
				orderRepository.existsByOrderTableAndStatusNot(servedEatInOrder.getOrderTable(), OrderStatus.COMPLETED))
				.thenReturn(false);

			// when
			Order completedOrder = orderService.complete(servedEatInOrder.getId());

			// then
			assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
			assertThat(servedEatInOrder.getOrderTable().getNumberOfGuests()).isZero();
			assertThat(servedEatInOrder.getOrderTable().isOccupied()).isFalse();
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
			Order validOrder = OrderFixture.createValidWithTypeAndStatus(
				OrderType.DELIVERY, OrderStatus.WAITING);

			Order anotherValidOrder = OrderFixture.createValidWithTypeAndStatus(
				OrderType.EAT_IN, OrderStatus.SERVED);

			when(orderRepository.findAll()).thenReturn(List.of(validOrder, anotherValidOrder));

			// when
			List<Order> orders = orderService.findAll();

			// then
			assertThat(orders).isNotEmpty();
			assertThat(orders.getFirst()).isEqualTo(validOrder);
			assertThat(orders.getLast()).isEqualTo(anotherValidOrder);
		}
	}
}