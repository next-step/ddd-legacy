package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
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

	@DisplayName("메뉴 가격과 주문항목의 가격은 반드시 동일해야 한다")
	@Test
	void menu_price_and_order_line_item_price_are_identical() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(15000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문항목의 메뉴는 반드시 노출되어 있는 상태여야 한다")
	@Test
	void menu_in_order_line_item_is_displayed() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, false, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문항목은 각각 메뉴를 한개씩 가진다")
	@Test
	void order_line_item_have_menu() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 전체를 조회할 수 있다")
	@Test
	void find_all_order() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
		// when & then
		List<Order> result = orderService.findAll();

		// then
		assertThat(result.size()).isOne();
		assertThat(result.get(0).getType()).isEqualTo(OrderType.EAT_IN);
		assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.SERVED);
	}

	@DisplayName("주문 종류가 매장 내 식사이고, 주문 상태가 완료인 매장테이블이 있을 경우 해당 매장 테이블을 비운다")
	@Test
	void clear_order_table_when_order_status_is_completed_and_it_is_a_eat_in_order() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));
		when(orderRepository.existsByOrderTableAndStatusNot(eq(orderTable), eq(OrderStatus.COMPLETED))).thenReturn(false);
		// when & then
		Order result = orderService.complete(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
		assertThat(orderTable.isEmpty()).isTrue();
		assertThat(orderTable.getNumberOfGuests()).isZero();
	}

	@DisplayName("주문 종류가 포장이거나 매장 내 식사일 경우에는 서빙완료되어야만 한다")
	@Test
	void when_order_type_is_eat_in_or_take_out_order_status_must_be_served() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.complete(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문 종류가 배달 일 경우 배달완료되어야만 한다")
	@Test
	void when_order_type_is_delivery_order_status_must_be_delivered() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.complete(uuid));
	}

	@DisplayName("완료 하려는 주문은 미리 등록되어 있어야 한다")
	@Test
	void when_to_make_completed_order_is_registered_in_advance() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderService.complete(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문 상태를 완료 상태로 변경할 수 있다")
	@Test
	void change_order_status_into_completed() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.DELIVERED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when
		Order result = orderService.complete(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
	}

	@DisplayName("배달완료 하려는 주문은 배달 중이 아닌 상태 값을 가질 수 없다")
	@Test
	void when_to_make_delivered_order_can_not_be_delivering() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.completeDelivery(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("배달완료 하려는 주문은 미리 등록되어 있어야 한다")
	@Test
	void when_to_make_delivered_order_is_registered_in_advance() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderService.completeDelivery(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문의 상태를 배달완료 상태로 변경할 수 있다")
	@Test
	void change_order_status_into_delivered() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.DELIVERING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when
		Order result = orderService.completeDelivery(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
	}

	@DisplayName("주문은 서빙완료 상태여야만 한다")
	@Test
	void order_status_must_be_served() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.startDelivery(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문 종류가 배달이어야만 한다")
	@Test
	void delivery_order_type_must_be_delivery() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.startDelivery(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("배달요청 하려는 주문은 미리 등록되어 있어야 한다")
	@Test
	void when_to_make_delivery_order_is_registered_in_advance() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderService.startDelivery(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문의 상태를 배달요청 상태로 변경할 수 있다")
	@Test
	void change_order_status_into_delivering() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.SERVED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when
		Order result = orderService.startDelivery(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
	}

	@DisplayName("서빙완료하려는 주문은 반드시 접수되어 있야야만 한다")
	@Test
	void when_to_serve_order_must_be_accepted() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.serve(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("서빙완료하려는 주문은 미리 등록되어 있어야 한다")
	@Test
	void when_to_serve_order_is_registed_in_advance() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.ACCEPTED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.serve(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문의 상태를 서빙완료 상태로 변경할 수 있다")
	@Test
	void change_order_status_into_the_served() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.ACCEPTED);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when
		Order result = orderService.serve(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
	}

	@DisplayName("접수하려는 주문의 상태는 대기 상태여야만 한다")
	@Test
	void when_to_accept_order_must_be_waiting() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.DELIVERING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> orderService.accept(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("접수하려는 주문은 미리 등록되어 있어야 한다")
	@Test
	void when_to_accept_order_is_registered_in_advance() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderService.accept(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문의 상태를 접수 상태로 변경할 수 있다")
	@Test
	void change_order_status_into_the_accepted() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(orderRepository.findById(uuid)).thenReturn(Optional.of(order));

		// when
		Order result = orderService.accept(uuid);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
	}

	@DisplayName("주문 종류가 매장 내 식사라면 해당 매장 테이블은 비어 있으면 안된다")
	@Test
	void wehn_to_have_eat_in_order_order_table_is_not_vacant() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블", 3);
		Order orderRequest = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블");

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
		when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("주문 종류가 매장 내 식사라면 매장 테이블이 미리 존재해야 한다")
	@Test
	void eat_in_order_must_have_order_table() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.EAT_IN, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
		when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문 종류가 배달일 경우 배달 주소를 반드시 가져야 한다")
	@Test
	void delivery_order_must_have_delivery_address() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 종류가 매장 내 식사가 아니라면(배달, 테이크아웃일 경우) 주문항목 갯수는 최소한 한 개 이상이어야 한다")
	@Test
	void order_line_item_which_is_not_eat_in_has_above_zero_quantity() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, -1L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블", 3);
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문항목은 반드시 1개 이상 존재해야 한다")
	@Test
	void order_line_item_is_must() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블", 3);
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.emptyList(), addressRequest, orderTableRequest);

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 종류는 필수적으로 들어가야 한다")
	@Test
	void order_type_is_must() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블", 3);
		Order orderRequest = new Order(null, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		// when & then
		assertThatThrownBy(() -> orderService.create(orderRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문을 만들 수 있으며, 주문은 만들어질 때 대기 상태를 가진다")
	@Test
	void create_order() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup("메뉴 그룹");
		Product productRequest = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProductRequest = new MenuProduct(productRequest, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroupRequest, true, Collections.singletonList(menuProductRequest));

		OrderLineItem orderLineItemRequest = new OrderLineItem(menuRequest, 2L, new BigDecimal(10000));
		String addressRequest = "주소";
		OrderTable orderTableRequest = new OrderTable("테이블");
		Order orderRequest = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItemRequest), addressRequest, orderTableRequest);

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		OrderTable orderTable = new OrderTable("테이블", 3);

		OrderLineItem orderLineItem = new OrderLineItem(menu, 2L, new BigDecimal(10000));
		String address = "주소";
		Order order = new Order(OrderType.DELIVERY, Collections.singletonList(orderLineItem), address, orderTable);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.WAITING);

		when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
		when(orderRepository.save(any())).thenReturn(order);

		// when
		Order result = orderService.create(orderRequest);

		// then
		assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
		assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
	}
}
