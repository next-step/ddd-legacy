package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  private OrderService orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private OrderTableRepository orderTableRepository;

  @Mock
  private KitchenridersClient kitchenridersClient;

  @BeforeEach
  void setUp() {
    this.orderService = new OrderService(
        orderRepository,
        menuRepository,
        orderTableRepository,
        kitchenridersClient
    );
  }

  @DisplayName("유효한 매장식사 주문정보를 입력하면 생성된 주문을 반환한다")
  @Test
  void givenValidEatInOrder_whenCreate_thenReturnOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(true);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
    given(orderRepository.save(any(Order.class))).willReturn(order);

    // when
    Order createdOrder = orderService.create(order);

    // then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(createdOrder.getOrderTableId()).isEqualTo(orderTable.getId());
    assertThat(createdOrder.getOrderLineItems()).hasSize(1);
    assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
        .contains(menu.getId());
  }

  @DisplayName("유효한 배달 주문정보를 입력하면 생성된 주문을 반환한다")
  @Test
  void givenValidDeliveryOrder_whenCreate_thenReturnOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setOrderLineItems(List.of(orderLineItem));
    order.setDeliveryAddress("서울시 강남구");

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderRepository.save(any(Order.class))).willReturn(order);

    // when
    Order createdOrder = orderService.create(order);

    // then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(createdOrder.getDeliveryAddress()).isEqualTo("서울시 강남구");
    assertThat(createdOrder.getOrderLineItems()).hasSize(1);
    assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
        .contains(menu.getId());
  }

  @DisplayName("유효한 테이크아웃 주문정보를 입력하면 생성된 주문을 반환한다")
  @Test
  void givenValidTakeOutOrder_whenCreate_thenReturnOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderRepository.save(any(Order.class))).willReturn(order);

    // when
    Order createdOrder = orderService.create(order);

    // then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(createdOrder.getOrderLineItems()).hasSize(1);
    assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
        .contains(menu.getId());
  }

  @DisplayName("주문타입이 올바르지 않으면 주문을 생성할 수 없다.")
  @Test
  void givenNotValidType_whenCreate_thenIllegalArgumentException() {
    // given
    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(null);

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문상품들은 비어있을 수 없다.")
  @NullAndEmptySource
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenEmptyOrderItems_whenCreate_thenIllegalArgumentException(
      List<OrderLineItem> orderLineItems) {
    // given
    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderLineItems(orderLineItems);

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문상품 메뉴가 존재하지 않으면 주문을 생성할 수 없다.")
  @Test
  void givenNoFoundMenu_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(true);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.emptyList());

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("배달 주문인 경우 주문상품수량이 0개 보다 작을 수 없다.")
  @Test
  void givenNotValidQuantityDelivery_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(-1);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setOrderLineItems(List.of(orderLineItem));
    order.setDeliveryAddress("서울시 강남구");

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("배달 주문인 경우 주문상품수량이 0개 보다 작을 수 없다.")
  @Test
  void givenNotValidQuantityTakeOut_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(-1);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문상품 메뉴가 진열 중이 아니면 주문을 생성할 수 없다.")
  @Test
  void givenHiddenMenu_whenCreate_thenIllegalStateException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(false);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문상품 가격과 메뉴가격이 일치하지 않으면 주문을 생성할 수 없다.")
  @Test
  void givenNotValidMenuPrice_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(24000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("배달 주문인 경우 배달주소가 비어있을 수 없다.")
  @NullAndEmptySource
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenEmptyDeliveryAddress_whenCreate_thenIllegalArgumentException(String deliveryAddress) {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setOrderLineItems(List.of(orderLineItem));
    order.setDeliveryAddress(deliveryAddress);

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("매장식사 주문인 경우 주문테이블이 존재하지 않으면 주문을 생성할 수 없다.")
  @Test
  void givenEmptyTable_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(true);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> orderService.create(order))
        .isInstanceOf(NoSuchElementException.class);
  }

  @DisplayName("매장식사 주문인 경우 주문테이블이 사용 중이어야 한다.")
  @Test
  void givenNotOccupied_whenCreate_thenIllegalArgumentException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(false);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문 ID를 입력받아 주문을 접수할 수 있다.")
  @Test
  void givenValidOrder_whenAccept_thenOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(false);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setStatus(OrderStatus.WAITING);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when
    Order acceptedOrder = orderService.accept(order.getId());

    // then
    assertThat(acceptedOrder.getId()).isEqualTo(order.getId());
    assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
  }

  @DisplayName("주문이 대기 중이 아닌 경우에는 접수할 수 없다.")
  @Test
  void givenNotWaiting_whenAccept_thenIllegalStateException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(false);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setStatus(OrderStatus.ACCEPTED);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.accept(order.getId()));
  }

  @DisplayName("배달 주문건인 경우 라이더스에게 주문ID, 메뉴가격, 배송지 정보를 통해 배달을 요청한다.")
  @Test
  void givenDelivery_whenAccept_thenOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.WAITING);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when
    Order acceptedOrder = orderService.accept(order.getId());

    // then
    assertThat(acceptedOrder.getId()).isEqualTo(order.getId());
    assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    verify(kitchenridersClient, times(1))
        .requestDelivery(order.getId(), BigDecimal.valueOf(23000).multiply(BigDecimal.valueOf(3)),
            "서울시 강남구");
  }

  @DisplayName("주문 ID를 입력받아 제공완료 상태로 변경할 수 있다.")
  @Test
  void givenServeOrder_whenServe_thenOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.ACCEPTED);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when
    Order servedOrder = orderService.serve(order.getId());

    // then
    assertThat(servedOrder.getId()).isEqualTo(order.getId());
    assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
  }

  @DisplayName("주문이 접수상태일 경우만 제공완료 상태로 변경 가능하다.")
  @Test
  void givenNotAcceptOrder_whenServe_thenIllegalStateException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.WAITING);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.serve(order.getId()));
  }

  @DisplayName("주문 ID를 입력받아 주문건을 배송 중 상태로 변경한다.")
  @Test
  void givenValidDeliveryOrder_whenStartDelivery_thenOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.SERVED);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when
    Order servedOrder = orderService.startDelivery(order.getId());

    // then
    assertThat(servedOrder.getId()).isEqualTo(order.getId());
    assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
  }

  @DisplayName("주문타입이 배달인 경우에만 진행할 수 있다.")
  @Test
  void givenNotDeliveryOrder_whenStartDelivery_thenIllegalStateException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setStatus(OrderStatus.SERVED);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.serve(order.getId()));
  }

  @DisplayName("주문이 제공완료 상태인 경우에는 배송 중 상태로 변경할 수 없다.")
  @Test
  void givenServedOrder_whenStartDelivery_thenIllegalStateException() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setStatus(OrderStatus.SERVED);
    order.setDeliveryAddress("서울시 강남구");
    order.setOrderLineItems(List.of(orderLineItem));

    given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

    // when & then
    assertThatIllegalStateException()
        .isThrownBy(() -> orderService.serve(order.getId()));
  }
}
