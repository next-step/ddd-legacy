package kitchenpos.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.OrderService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.application.infra.FakeKitchenridersClient;
import kitchenpos.application.menu.InMemoryMenuRepository;
import kitchenpos.application.order_table.InMemoryOrderTableRepository;
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

public class OrderServiceTest {

  private OrderService orderService;

  private OrderTableRepository orderTableRepository;

  private MenuRepository menuRepository;

  @BeforeEach
  public void init() {
    this.menuRepository = new InMemoryMenuRepository();
    this.orderTableRepository = new InMemoryOrderTableRepository();
    OrderRepository orderRepository = new InMemoryOrderRepository();
    KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
    orderService = new OrderService(orderRepository, this.menuRepository, this.orderTableRepository,
        kitchenridersClient);
  }

  @DisplayName("주문을 접수할 수 있다.")
  @Test
  public void register() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    assertThat(order).isNotNull();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
  }

  @DisplayName("주문 유형이 없으면 IllegalArgumentException 예외 처리를 한다.")
  @NullSource
  @ParameterizedTest
  public void registerFailBecauseType(OrderType orderType) {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(orderType, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문 항목이 없거나 비었다면 IllegalArgumentException 예외 처리를 한다.")
  @NullAndEmptySource
  @ParameterizedTest
  public void registerFailBecauseOrderLineAbsence(List<OrderLineItem> orderLineItems) {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    Order order = OrderFixture.create(OrderType.EAT_IN, orderLineItems,"주소", orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("존재하지않는 메뉴를 주문 항목에 담았다면 IllegalArgumentException 예외 처리를 한다.")
  @Test
  public void registerFailBecauseMenuNotFound() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    Menu notExistMenu = MenuFixture.createDefaultMenu();
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    OrderLineItem notExistsMenuOrderLineItem = OrderFixture.createOrderLineItem(notExistMenu, 3L, 200L);

    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem, notExistsMenuOrderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("배달과 포장 주문항목의 수량이 0개 미만이라면 IllegalArgumentException 예외 처리를 한다.")
  @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
  @ParameterizedTest
  public void registerFailBecauseOrderLineAbsence(OrderType orderType) {
    Menu menu = MenuFixture.createDefaultMenu(false);
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, -1L, 200L);
    Order order = OrderFixture.create(orderType, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("비공개 메뉴를 주문항목에 담을 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void registerFailBecauseHideMenu() {
    Menu menu = MenuFixture.createDefaultMenu(false);
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("등록된 메뉴의 가격과 주문항목으로 담은 메뉴의 가격이 다를 경우 IllegalArgumentException 예외 처리를 한다.")
  @Test
  public void registerFailBecauseMenuPrice() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 300L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문 유형이 배달일 경우 배달 주소가 없거나 비었다면 IllegalArgumentException 예외 처리를 한다.")
  @NullAndEmptySource
  @ParameterizedTest
  public void registerFailBecauseDeliveryAddress(String deliveryAddress) {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),deliveryAddress, orderTable);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문 유형이 매장식사일 경우 주문테이블이 존재하지 않는다면 NoSuchElementException 예외 처리를 한다.")
  @Test
  public void registerFailBecauseOrderTable() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(NoSuchElementException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문 유형이 매장식사일 경우 주문테이블이 사용중이지 않다면 IllegalStateException 예외 처리를 한다.")
  @Test
  public void registerFailBecauseOrderTableNotOccupied() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", false);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> orderService.create(order));
  }

  @DisplayName("주문을 수락할 수 있다.")
  @Test
  public void accept() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
  }

  @DisplayName("주문 상태가 대기중이 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void acceptFailBecauseStatus() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    orderService.accept(orderId);
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.accept(orderId));
  }

  @DisplayName("주문을 준비완료할 수 있다.")
  @Test
  public void served() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
  }

  @DisplayName("주문 상태가 수락이 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void servedFailBecauseStatus() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.serve(orderId));
  }

  @DisplayName("주문을 배달 시작할 수 있다.")
  @Test
  public void startDelivery() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    order = orderService.startDelivery(order.getId());

    assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
  }

  @DisplayName("주문 유형이 배달이 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void startDeliveryFailBecauseType() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.startDelivery(orderId));
  }

  @DisplayName("주문 상태가 준비완료가 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void startDeliveryFailBecauseStatus() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.startDelivery(orderId));
  }

  @DisplayName("주문을 배달 완료할 수 있다.")
  @Test
  public void completeDelivery() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    order = orderService.startDelivery(order.getId());
    order = orderService.completeDelivery(order.getId());

    assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
  }

  @DisplayName("주문 상태가 배달시작이 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void completeDeliveryFailBecauseStatus() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.completeDelivery(orderId));
  }

  @DisplayName("주문을 완료할 수 있다.")
  @Test
  public void completeOrder() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    order = orderService.complete(order.getId());

    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }

  @DisplayName("배달 주문의 상태가 배달완료가 아닐 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void completeDeliveryOrderFailBecauseStatus() {
    Menu menu = MenuFixture.createDefaultMenu();
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
    Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() ->orderService.complete(orderId));
  }

  @DisplayName("포장과 매장식사 주문의 상태가 준비완료가 아니라면 IllegalStateException 예외 처리를 한다.")
  @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
  @ParameterizedTest
  public void completeFailBecauseNotServed(OrderType orderType) {
    Menu menu = MenuFixture.createDefaultMenu(true);
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
    Order order = OrderFixture.create(orderType, List.of(orderLineItem),"주소", orderTable);
    order = orderService.create(order);
    UUID orderId = order.getId();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> orderService.complete(orderId));
  }

  @DisplayName("매장식사 주문이 완료되고 다른 주문이 있다면 주문테이블을 초기화하지 않는다.")
  @Test
  public void completeAndOrderTableNotClear() {
    Menu menu = MenuFixture.createDefaultMenu(true);
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    Order order2 = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    orderService.create(order2);
    order = orderService.create(order);
    order = orderService.accept(order.getId());
    order = orderService.serve(order.getId());
    order = orderService.complete(order.getId());
    assertThat(order.getOrderTable().isOccupied()).isTrue();
  }

  @DisplayName("주문 전체를 조회할 수 있다.")
  @Test
  public void findAll() {
    Menu menu = MenuFixture.createDefaultMenu(true);
    menuRepository.save(menu);
    OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
    orderTableRepository.save(orderTable);
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    Order order2 = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    orderService.create(order);
    orderService.create(order2);
    List<Order> orders = orderService.findAll();
    assertThat(orders).hasSize(2);
  }

}
