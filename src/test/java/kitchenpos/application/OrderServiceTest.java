package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	private static final Long ZERO = 0L;
	private static final Long NEGATIVE_NUM = -1L;
	private static final Long POSITIVE_NUM = 1L;
	private static final BigDecimal PRICE = BigDecimal.valueOf(10000L);
	private static final BigDecimal PRICE_ZERO = BigDecimal.ZERO;
	private static final String ADDRESS = "Address";
	public static final UUID RANDOM_UUID = UUID.randomUUID();

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private MenuRepository menuRepository;
	@Mock
	private OrderTableRepository orderTableRepository;
	@Mock
	private KitchenridersClient kitchenridersClient;

	@InjectMocks
	OrderService orderService;

	private static Stream<String> getAddress() {
		return Stream.of(
			"",
			null
		);
	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = {"DELIVERY"})
	@DisplayName("배달인 경우 주문 시작]")
	void createOrderDelivery(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));
		when(request.getDeliveryAddress()).thenReturn(ADDRESS);
		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(orderLineItem.getPrice()).thenReturn(PRICE);

		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(true);
		when(menuRepository.findById(any()).get().getPrice()).thenReturn(PRICE);

		orderService.create(request);

	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = {"TAKEOUT", "DELIVERY"})
	@DisplayName("주문 타입이 배달이거나 포장일 경우, 각각 상품품목 수량이 0개 이상이어야 합니다.")
	void checkQuantity(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));
		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(NEGATIVE_NUM);

		//then
		assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@MethodSource("getAddress")
	@DisplayName("배달인 경우 주문 시작 : 배달 주문인 경우 배달주소가 필요합니다.")
	void DeliveryNeedAddress(String address) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(OrderType.DELIVERY);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));
		//when
		when(request.getDeliveryAddress()).thenReturn(address);
		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(orderLineItem.getPrice()).thenReturn(PRICE);
		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(true);
		when(menuRepository.findById(any()).get().getPrice()).thenReturn(PRICE);

		assertThatThrownBy(() -> orderService.create(request))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = {"EAT_IN"})
	@DisplayName("손님은 가게에 주문을 할 수 있습니다.")
	void createOrderEatIn(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));

		when(orderTableRepository.findById(any())).thenReturn(Optional.of(mock(OrderTable.class)));
		when(orderTableRepository.findById(any()).get().isEmpty()).thenReturn(true);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(orderLineItem.getPrice()).thenReturn(PRICE);

		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(true);
		when(menuRepository.findById(any()).get().getPrice()).thenReturn(PRICE);

		orderService.create(request);
		verify(orderTableRepository, times(2)).findById(any());

	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = {"EAT_IN"})
	@DisplayName("매장 내 식사 주문인 경우 주문 테이블이 비어있어야 합니다.")
	void EatInNeedOrderTable(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));

		when(orderTableRepository.findById(any())).thenReturn(Optional.of(mock(OrderTable.class)));
		when(orderTableRepository.findById(any()).get().isEmpty()).thenReturn(false);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(orderLineItem.getPrice()).thenReturn(PRICE);

		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(true);
		when(menuRepository.findById(any()).get().getPrice()).thenReturn(PRICE);

		assertThatThrownBy(() -> orderService.create(request))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class)
	@DisplayName("메뉴가 숨김 처리가 되어 있으면 주문할 수 없습니다.")
	void menuDisabledFalse(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));
		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		//when
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(false);

		//then
		assertThatThrownBy(() -> orderService.create(request))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class)
	@DisplayName("메뉴 가격과 주문하려는 메뉴의 상품품목의 가격이 같아야 합니다.")
	void checkMenuPrice(OrderType orderType) {
		//given
		Order request = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);
		Menu menu = mock(Menu.class);

		when(request.getType()).thenReturn(orderType);
		when(request.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(orderLineItem.getPrice()).thenReturn(PRICE);

		when(menuRepository.findById(any())).thenReturn(Optional.of(mock(Menu.class)));
		when(menuRepository.findById(any()).get().isDisplayed()).thenReturn(true);
		when(menuRepository.findById(any()).get().getPrice()).thenReturn(PRICE_ZERO);

		assertThatThrownBy(() -> orderService.create(request))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@DisplayName("가게 점주는 주문 요청을 허락할 수 있습니다.(배달 제외)")
	@EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = EnumSource.Mode.EXCLUDE)
	void acceptOrderExcludeDelivery(OrderType orderType) {
		//given
		Order order = mock(Order.class);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(order.getStatus()).thenReturn(OrderStatus.WAITING);
		//when
		when(order.getType()).thenReturn(orderType);
		//then
		orderService.accept(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.ACCEPTED);
	}

	@ParameterizedTest
	@DisplayName("사용자의 주문 방식이 배달이면 배달을 요청합니다.")
	@EnumSource(value = OrderType.class,  names = {"DELIVERY"})
	void acceptOrderDeliveryType(OrderType orderType) {
		//given
		Order order = mock(Order.class);
		OrderLineItem orderLineItem = mock(OrderLineItem.class);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(order.getStatus()).thenReturn(OrderStatus.WAITING);
		when(order.getOrderLineItems()).thenReturn(Collections.singletonList(orderLineItem));
		when(orderLineItem.getMenu()).thenReturn(mock(Menu.class));
		when(orderLineItem.getMenu().getPrice()).thenReturn(PRICE);
		when(orderLineItem.getQuantity()).thenReturn(POSITIVE_NUM);
		when(order.getDeliveryAddress()).thenReturn(ADDRESS);
		//when
		when(order.getType()).thenReturn(orderType);
		//then
		orderService.accept(RANDOM_UUID);
		verify(kitchenridersClient).requestDelivery(RANDOM_UUID, PRICE, order.getDeliveryAddress());
	}

	@ParameterizedTest
	@DisplayName("주문의 상태가 수락전 주문만 수락이 가능합니다.")
	@EnumSource(value = OrderStatus.class, names = {"WAITING"}, mode = EnumSource.Mode.EXCLUDE)
	void acceptOrderOnlyWaitingType(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		//when
		when(order.getStatus()).thenReturn(orderStatus);
		//then
		assertThatThrownBy(() -> orderService.accept(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("가게 점주는 주문 상태를 준비 상태로 변경합니다.")
	@EnumSource(value = OrderStatus.class, names = {"ACCEPTED"})
	void serve(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);
		//when
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(order.getStatus()).thenReturn(orderStatus);
		//then
		orderService.serve(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.SERVED);
	}

	@ParameterizedTest
	@DisplayName("기존 주문의 상태는 수락이어야 합니다.")
	@EnumSource(value = OrderStatus.class, names = {"ACCEPTED"}, mode = EnumSource.Mode.EXCLUDE)
	void serveOnlyAccepted() {
		//given
		Order order = mock(Order.class);
		//when
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(order.getStatus()).thenReturn(OrderStatus.ACCEPTED);
		//then
		orderService.serve(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.SERVED);
	}

	@Test
	@DisplayName("주문 상태를 배송중 상태로 변경합니다.")
	void startDelivery() {
		//given
		Order order = mock(Order.class);
		//when
		when(order.getType()).thenReturn(OrderType.DELIVERY);
		when(order.getStatus()).thenReturn(OrderStatus.SERVED);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		orderService.startDelivery(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.DELIVERING);
	}

	@ParameterizedTest
	@DisplayName("기존 주문 방식은 배달이어야 합니다.")
	@EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = EnumSource.Mode.EXCLUDE)
	void startDeliveryOnlyDelivery(OrderType orderType) {
		//given
		Order order = mock(Order.class);
		//when
		when(order.getType()).thenReturn(orderType);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.startDelivery(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("기존 주문 상태는 준비중이어야 합니다.")
	@EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
	void startDeliveryOnlyServed(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);
		//when
		when(order.getType()).thenReturn(OrderType.DELIVERY);
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.startDelivery(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("배달 타입의 주문 상태를 완료 상태로 변경합니다.")
	@EnumSource(value = OrderStatus.class, names = {"DELIVERING"})
	void completeDelivery(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);
		//when
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		orderService.completeDelivery(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.DELIVERED);
	}

	@ParameterizedTest
	@DisplayName("완료 상태로 변경할 때, 기존 주문 상태는 배송중이어야 합니다.")
	@EnumSource(value = OrderStatus.class, names = {"DELIVERING"}, mode = EnumSource.Mode.EXCLUDE)
	void completeDeliveryOnlyDelivering(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);
		//when
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.completeDelivery(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("배달 타입의 주문 상태를 완료 상태로 변경합니다.")
	@EnumSource(value = OrderType.class, names = {"DELIVERY"})
	void completeDelivery(OrderType orderType) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(orderType);
		when(order.getStatus()).thenReturn(OrderStatus.DELIVERED);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		//then
		orderService.complete(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.COMPLETED);
	}

	@ParameterizedTest
	@DisplayName("배달 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 배송 중일 때만 변경을 허락합니다.")
	@EnumSource(value = OrderStatus.class, names = {"DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
	void completeDeliveryOnlyDelivered(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(OrderType.DELIVERY);
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		//then
		assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("포장 타입의 주문 상태를 완료 상태로 변경합니다")
	@EnumSource(value = OrderStatus.class, names = {"SERVED"})
	void completeTakeOutAndEanIn(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(OrderType.TAKEOUT);
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		//then
		orderService.complete(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.COMPLETED);
	}

	@ParameterizedTest
	@DisplayName("포장 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 준비중이어야 합니다. ")
	@EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
	void completeTakeOutOnlyServed(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(OrderType.TAKEOUT);
		when(order.getStatus()).thenReturn(orderStatus);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		//then
		assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@ParameterizedTest
	@DisplayName("매장 식사 타입의 주문 상태를 완료 상태로 변경합니다")
	@EnumSource(value = OrderStatus.class, names = {"SERVED"})
	void completeEanIn(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(OrderType.EAT_IN);
		when(order.getStatus()).thenReturn(orderStatus);
		when(order.getOrderTable()).thenReturn(mock(OrderTable.class));

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

		//then
		orderService.complete(RANDOM_UUID);
		verify(order).setStatus(OrderStatus.COMPLETED);
	}

	@ParameterizedTest
	@DisplayName("매장 식사 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 준비중이어야 합니다. ")
	@EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
	void completeEatInOnlyServed(OrderStatus orderStatus) {
		//given
		Order order = mock(Order.class);

		//when
		when(order.getType()).thenReturn(OrderType.EAT_IN);
		when(order.getStatus()).thenReturn(orderStatus);

		when(orderRepository.findById(any())).thenReturn(Optional.of(order));

		//then
		assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("가게 점주는 주문 정보를 모두 조회할 수 있습니다.")
	void findAll() {
		//given
		when(orderRepository.findAll()).thenReturn(Collections.singletonList(mock(Order.class)));

		//then
		orderService.findAll();
		verify(orderRepository).findAll();
	}
}