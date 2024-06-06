package kitchenpos.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.OrderService;
import kitchenpos.application.fake.repository.InMemoryOrderRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.application.fake.infra.FakeKitchenridersClient;
import kitchenpos.application.fake.repository.InMemoryMenuRepository;
import kitchenpos.application.fake.repository.InMemoryOrderTableRepository;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

public class OrderServiceTest {

  private OrderService orderService;

  private OrderTableRepository orderTableRepository;

  private MenuRepository menuRepository;
  private OrderRepository orderRepository;
  private Menu menu;
  private OrderTable orderTable;

  @BeforeEach
  public void init() {
    this.menuRepository = new InMemoryMenuRepository();
    this.orderTableRepository = new InMemoryOrderTableRepository();
    this.orderRepository = new InMemoryOrderRepository();
    KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
    orderService = new OrderService(orderRepository, this.menuRepository, this.orderTableRepository,
        kitchenridersClient);
    this.menu = menuRepository.save(MenuFixture.createDefaultMenu());
    this.orderTable = orderTableRepository.save(OrderTableFixture.create("주문테이블", true));
  }

  @DisplayName("주문을 접수할 수 없다.")
  @Nested
  class Register {
    @DisplayName("성공")
    @Test
    public void register() {
      OrderType orderType = OrderType.DELIVERY;
      String deliveryAddress = "주소";
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(orderType, List.of(orderLineItem),deliveryAddress, orderTable);
      order = orderService.create(order);
      assertThat(order.getId()).isNotNull();
      assertThat(order.getDeliveryAddress()).isEqualTo(deliveryAddress);
      assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
      assertThat(order.getType()).isEqualTo(orderType);
    }
    @DisplayName("주문 유형은 필수로 선택해야한다.")
    @NullSource
    @ParameterizedTest
    public void registerFailBecauseType(OrderType orderType) {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(orderType, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문항목은 1개 이상이어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void registerFailBecauseOrderLineAbsence(List<OrderLineItem> orderLineItems) {
      Order order = OrderFixture.create(OrderType.EAT_IN, orderLineItems,"주소", orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("존재하지않는 메뉴는 주문항목에 담을 수 없다.")
    @Test
    public void registerFailBecauseMenuNotFound() {
      Menu notExistMenu = MenuFixture.createDefaultMenu();
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      OrderLineItem notExistsMenuOrderLineItem = OrderFixture.createOrderLineItem(notExistMenu, 3L, 200L);

      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem, notExistsMenuOrderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 유형이 배달, 포장일 경우 주문 항목의 수량은 0개 이상이어야한다.")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    @ParameterizedTest
    public void registerFailBecauseOrderLineAbsence(OrderType orderType) {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, -1L, 200L);
      Order order = OrderFixture.create(orderType, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("공개된 메뉴만 주문항목에 담을 수 있다.")
    @Test
    public void registerFailBecauseHideMenu() {
      Menu menu = MenuFixture.createDefaultMenu(false);
      menuRepository.save(menu);
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("등록된 메뉴의 가격과 주문항목으로 담은 메뉴의 가격은 같아야한다.")
    @Test
    public void registerFailBecauseMenuPrice() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 300L);
      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 유형이 배달일 경우 배달 주소는 0자 이상이어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void registerFailBecauseDeliveryAddress(String deliveryAddress) {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem),deliveryAddress, orderTable);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 유형이 매장식사일 경우 주문테이블이 존재해야한다.")
    @Test
    public void registerFailBecauseOrderTable() {
      OrderTable orderTable = OrderTableFixture.create("주문테이블", true);
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(NoSuchElementException.class)
          .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 유형이 매장식사일 경우 사용 처리된 주문테이블이어야 한다.")
    @Test
    public void registerFailBecauseOrderTableNotOccupied() {
      OrderTable orderTable = OrderTableFixture.create("주문테이블", false);
      orderTableRepository.save(orderTable);
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> orderService.create(order));
    }
  }

  @DisplayName("주문을 수락할 수 있다.")
  @Nested
  class Accept {
    @DisplayName("성공")
    @Test
    public void accept() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.WAITING, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      order = orderService.accept(order.getId());
      assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문의 상태는 대기중 이어야한다.")
    @Test
    public void acceptFailBecauseStatus() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.COMPLETED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      UUID orderId = order.getId();
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() ->orderService.accept(orderId));
    }
  }


  @DisplayName("주문을 준비완료할 수 있다.")
  @Nested
  class Served {
    @DisplayName("성공")
    @Test
    public void served() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.ACCEPTED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      order = orderService.serve(order.getId());
      assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문의 상태는 수락이어야한다.")
    @Test
    public void servedFailBecauseStatus() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.SERVED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      UUID orderId = order.getId();
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() ->orderService.serve(orderId));
    }
  }

  @DisplayName("주문을 배송시작할 수 있다.")
  @Nested
  class StartDelivery {
    @DisplayName("성공")
    @Test
    public void startDelivery() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.DELIVERY, OrderStatus.SERVED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      order = orderService.startDelivery(order.getId());

      assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문의 유형은 배달 이어야한다.")
    @Test
    public void startDeliveryFailBecauseType() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      UUID orderId = order.getId();
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() ->orderService.startDelivery(orderId));
    }

    @DisplayName("주문의 상태는 준비완료 이어야한다.")
    @Test
    public void startDeliveryFailBecauseStatus() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.DELIVERY, OrderStatus.COMPLETED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      UUID orderId = order.getId();
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() ->orderService.startDelivery(orderId));
    }
  }

  @DisplayName("주문을 배송완료할 수 있다.")
  @Nested
  class CompleteDelivery {
    @DisplayName("성공")
    @Test
    public void completeDelivery() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.DELIVERY, OrderStatus.DELIVERING, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      order = orderService.completeDelivery(order.getId());

      assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문의 상태는 배달중 이어야한다.")
    @Test
    public void completeDeliveryFailBecauseStatus() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.DELIVERY, OrderStatus.COMPLETED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      UUID orderId = order.getId();
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() ->orderService.completeDelivery(orderId));
    }
  }

  @DisplayName("주문을 완료할 수 있다.")
  @Nested
  class CompleteOrder {
    @DisplayName("성공")
    @Test
    public void completeOrder() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.SERVED, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);
      order = orderService.complete(order.getId());

      assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문을 완료할 수 없다.")
    @Nested
    class CompleteFail {
      @DisplayName("주문의 유형이 배달일 경우 상태가 배달완료 여야한다.")
      @Test
      public void completeDeliveryOrderFailBecauseStatus() {
        OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
        Order order = OrderFixture.create(OrderType.DELIVERY, OrderStatus.DELIVERING, List.of(orderLineItem),"주소", orderTable);
        order = orderRepository.save(order);
        UUID orderId = order.getId();
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() ->orderService.complete(orderId));
      }

      @DisplayName("주문의 유형이 포장이거나 매장식사일 경우 상태가 준비완료 여야한다.")
      @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
      @ParameterizedTest
      public void completeFailBecauseNotServed(OrderType orderType) {
        OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
        Order order = OrderFixture.create(orderType, OrderStatus.COMPLETED, List.of(orderLineItem),"주소", orderTable);
        order = orderRepository.save(order);
        UUID orderId = order.getId();
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(orderId));
      }
    }

    @DisplayName("매장식사 주문이 완료되고 다른 주문이 있다면 주문테이블을 초기화하지 않는다.")
    @Test
    public void completeAndOrderTableNotClear() {
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.SERVED, List.of(orderLineItem),"주소", orderTable);
      Order order2 = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
      orderService.create(order2);
      order = orderRepository.save(order);
      order = orderService.complete(order.getId());
      assertThat(order.getOrderTable().isOccupied()).isTrue();
    }
  }

  @DisplayName("주문 전체를 조회할 수 있다.")
  @Test
  public void findAll() {
    OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1L, 200L);
    Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    Order order2 = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem),"주소", orderTable);
    orderService.create(order);
    orderService.create(order2);
    List<Order> orders = orderService.findAll();
    assertThat(orders).hasSize(2);
  }

}
