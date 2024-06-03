package kitchenpos.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import kitchenpos.application.MenuService;
import kitchenpos.application.OrderService;
import kitchenpos.domain.common.FakeUuidBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.menu.FakeMenuGroupRepository;
import kitchenpos.domain.menu.FakeMenuRepository;
import kitchenpos.domain.order.FakeOrderRepository;
import kitchenpos.domain.order.FakeOrderTableRepository;
import kitchenpos.domain.product.FakeProductRepository;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.DefaultKitchenridersClient;
import kitchenpos.infra.FakeBadWordsValidator;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.BadWordsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

  private MenuGroupRepository menuGroupRepository;
  private MenuRepository menuRepository;
  private MenuService menuService;
  private ProductRepository productRepository;
  private BadWordsValidator badWordsValidator;
  private OrderService orderService;
  private OrderRepository orderRepository;
  private OrderTableRepository orderTableRepository;
  private KitchenridersClient kitchenridersClient;
  private Menu udonForTwo;

  @BeforeEach
  void setUp() {
    menuRepository = new FakeMenuRepository();
    menuGroupRepository = new FakeMenuGroupRepository();
    badWordsValidator = new FakeBadWordsValidator();
    productRepository = new FakeProductRepository();
    orderRepository = new FakeOrderRepository();
    orderTableRepository = new FakeOrderTableRepository();
    kitchenridersClient = new DefaultKitchenridersClient();

    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
        badWordsValidator);

    productRepository.save(
        ProductFixture.udon);
    productRepository.save(
        ProductFixture.ramen
    );

    menuGroupRepository.save(OrderFixture.menuGroup);
    udonForTwo = menuService.create(OrderFixture.menu);

    OrderFixture.orderTable.setOccupied(true);
    orderTableRepository.save(OrderFixture.orderTable);

    orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
        kitchenridersClient);

  }

  @Nested
  @DisplayName("주문(Order)을 생성한다.")
  class OrderCreation {

    @Test
    @DisplayName("주문(Order)을 생성할 수 있다.")
    void createOrder() {
      Order actual = orderService.create(OrderFixture.EAT_IN_ORDER);

      assertAll(
          () -> assertThat(actual.getId()).isNotNull(),
          () -> assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN),
          () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
      );
    }

    @DisplayName("메뉴가 생성된 상태이고 전시 되어있어야한다.")
    @Test
    void failToCreateOrderWithHiddenMenu() {
      Menu hiddenMenu = menuService.hide(udonForTwo.getId());

      assertThatIllegalStateException()
          .isThrownBy(() -> orderService.create(OrderFixture.EAT_IN_ORDER));
    }

    @DisplayName("주문은 메뉴 1개 이상으로 구성되어야 한다.")
    @Test
    void faileToCreateWithZeroOrderLineItem() {
      Order order = OrderFixture.createTakeOutOrder(OrderFixture.orderLineItem);
      order.setOrderLineItems(Collections.EMPTY_LIST);

      assertThatIllegalArgumentException()
          .isThrownBy(() -> orderService.create(order));
    }

  }

  @Nested
  @DisplayName("모든 주문리스트를 조회할 수 있다.")
  class OrderListView {

    @Test
    @DisplayName("모든 주문리스트를 조회할 수 있다.")
    void viewOrderList() {
      Order eatInOrder = orderService.create(OrderFixture.EAT_IN_ORDER);
      Order deliveryOrder = orderService.create(OrderFixture.DELIVERY_ORDER);

      List<Order> actual = orderService.findAll();

      assertThat(actual).contains(eatInOrder, deliveryOrder);
    }

  }

  @Nested
  @DisplayName("주문을 접수(accept)한다.")
  class orderAcceptance {

    @Test
    @DisplayName("주문의 상태를 `접수`로 변경한다.")
    void changeOrderStatusToAccpetedWhenAccepted() {

      Order eatInOrder = orderService.create(OrderFixture.EAT_IN_ORDER);

      orderService.accept(eatInOrder.getId());

      assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

  }

  @Nested
  @DisplayName("주문을 손님에게 전달한다.")
  class OrderPass {

    @DisplayName("주문의 상태가 `접수`이어야 한다.")
    @Test
    void passOrderWithOrderStatusAccepted() {
      Order actual = orderService.create(OrderFixture.EAT_IN_ORDER);

      orderService.accept(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

      orderService.serve(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);

      actual.setStatus(OrderStatus.DELIVERING);
      orderRepository.save(actual);
      assertThatIllegalStateException()
          .isThrownBy(() -> orderService.serve(actual.getId()));
    }

  }

  @Nested
  @DisplayName("주문을 배달완료 한다.")
  class orderDeliveryComplete {

    @DisplayName("주문 상태가 `배달중`이어야 한다.")
    @Test
    void deliverOrderWithOrderStatusDelivering() {
      Order actual = orderService.create(OrderFixture.DELIVERY_ORDER);
      orderService.accept(actual.getId());
      orderService.serve(actual.getId());
      orderService.startDelivery(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);

      orderService.completeDelivery(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);

      actual.setStatus(OrderStatus.SERVED);
      orderRepository.save(actual);

      assertThatIllegalStateException()
          .isThrownBy(() -> orderService.completeDelivery(actual.getId()));
    }
  }

  @Nested
  @DisplayName("주문을 완료한다.")
  class OrderComplete {

    @Test
    @DisplayName("주문 종류가 `매장내` 이거나 `포장` 일 경우에 주문 상태가 `전달`이 아니라면 주문 완료 할 수 없다.")
    void failToCompleteWithOrderStatusServed() {

      Order eatInActual = orderService.create(OrderFixture.EAT_IN_ORDER);
      Order takeOutActual = orderService.create(OrderFixture.TAKEOUT_ORDER);

      orderService.accept(eatInActual.getId());
      orderService.serve(eatInActual.getId());
      orderService.accept(takeOutActual.getId());
      orderService.serve(takeOutActual.getId());

      assertThat(eatInActual.getStatus()).isEqualTo(OrderStatus.SERVED);
      assertThat(takeOutActual.getStatus()).isEqualTo(OrderStatus.SERVED);

      orderService.complete(eatInActual.getId());
      orderService.complete(takeOutActual.getId());

      assertThat(eatInActual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
      assertThat(takeOutActual.getStatus()).isEqualTo(OrderStatus.COMPLETED);

      takeOutActual.setStatus(OrderStatus.WAITING);
      orderRepository.save(takeOutActual);

      assertThatIllegalStateException()
          .isThrownBy(() -> orderService.complete(takeOutActual.getId()));
    }

    @DisplayName("주문 종류가 `배달` 일 때, 주문 상태가 `배달완료` 된 주문만 완료 할 수 있다.")
    @Test
    void completeDeliveryOrderWithOnlyDelivered() {
      Order actual = orderService.create(OrderFixture.DELIVERY_ORDER);
      orderService.accept(actual.getId());
      orderService.serve(actual.getId());
      orderService.startDelivery(actual.getId());
      orderService.completeDelivery(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);

      orderService.complete(actual.getId());

      assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 종류가 `매장내` 일 때, 주문 테이블의 상태를 초기화한다.")
    void clearOrderTableWithCompleteOrder() {
      Order eatInOrder = OrderFixture.EAT_IN_ORDER;

      Order eatInActual = orderService.create(eatInOrder);

      orderService.accept(eatInActual.getId());
      orderService.serve(eatInActual.getId());
      orderService.complete(eatInActual.getId());
      assertAll(
          () -> assertThat(eatInActual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
          () -> assertThat(eatInActual.getOrderTable().isOccupied()).isFalse(),
          () -> assertThat(eatInActual.getOrderTable().getNumberOfGuests()).isZero()
      );
    }
  }
}
