package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
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
import kitchenpos.domain.RidersClient;
import kitchenpos.infra.FakeRidersClient;
import kitchenpos.infra.InMemoryMenuRepository;
import kitchenpos.infra.InMemoryOrderRepository;
import kitchenpos.infra.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  private final OrderRepository orderRepository = new InMemoryOrderRepository();
  private final MenuRepository menuRepository = new InMemoryMenuRepository();
  private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
  private final RidersClient ridersClient = new FakeRidersClient();

  private OrderService orderService;

  @BeforeEach
  void setUp() {
    this.orderService = new OrderService(
        orderRepository,
        menuRepository,
        orderTableRepository,
        ridersClient
    );
  }

  @Nested
  @DisplayName("매장 주문")
  class EatInOrder {

    @Nested
    @DisplayName("주문을 생성할 때")
    class WhenOrderCreate {

      @DisplayName("유효한 매장 주문정보를 입력하면 생성된 주문을 반환한다")
      @Test
      void givenValidEatInOrder_whenCreate_thenReturnOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order order = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        // when
        Order createdOrder = orderService.create(order);

        // then
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(createdOrder.getOrderTable().getId()).isEqualTo(orderTable.getId());
        assertThat(createdOrder.getOrderLineItems()).hasSize(1);
        assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenu)
            .contains(menu);
      }

      @DisplayName("주문타입이 올바르지 않으면 주문을 생성할 수 없다.")
      @Test
      void givenNotValidType_whenCreate_thenIllegalArgumentException() {
        // given
        Order creationRequestOrder = new Order();
        creationRequestOrder.setType(null);

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(creationRequestOrder));
      }

      @DisplayName("주문상품들은 비어있을 수 없다.")
      @NullAndEmptySource
      @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
      void givenEmptyOrderItems_whenCreate_thenIllegalArgumentException(
          List<OrderLineItem> orderLineItems) {
        // given
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(orderLineItems);

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(order));
      }

      @DisplayName("주문상품 메뉴가 존재하지 않으면 주문을 생성할 수 없다.")
      @Test
      void givenNoFoundMenu_whenCreate_thenIllegalArgumentException() {
        // given
        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order order = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(OrderFixtures.createRequestOrderLineItem(UUID.randomUUID(), BigDecimal.valueOf(23000), 3)));

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(order));
      }

      @DisplayName("주문테이블이 존재하지 않으면 주문을 생성할 수 없다.")
      @Test
      void givenEmptyTable_whenCreate_thenIllegalArgumentException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        Order order = OrderFixtures.createRequestEatInOrder(
            UUID.randomUUID(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(NoSuchElementException.class);
      }

      @DisplayName("주문테이블이 사용 중이어야 한다.")
      @Test
      void givenNotOccupied_whenCreate_thenIllegalArgumentException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 0, false));

        Order order = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.create(order));
      }
    }

    @Nested
    @DisplayName("주문을 접수할 때")
    class WhenOrderAccept {

      @DisplayName("주문 ID를 입력받아 주문을 접수할 수 있다.")
      @Test
      void givenValidOrder_whenAccept_thenOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);

        // when
        Order acceptedOrder = orderService.accept(order.getId());

        // then
        assertThat(acceptedOrder.getId()).isEqualTo(order.getId());
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
      }

      @DisplayName("주문이 대기 중이 아닌 경우에는 접수할 수 없다.")
      @Test
      void givenNotWaiting_whenAccept_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);
        orderService.accept(order.getId());

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.accept(order.getId()));
      }

    }

    @Nested
    @DisplayName("주문을 서빙할 때")
    class WhenOrderServer {

      @DisplayName("주문 ID를 입력받아 서빙할 수 있다")
      @Test
      void givenServeOrder_whenServe_thenOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);
        orderService.accept(order.getId());

        // when
        Order servedOrder = orderService.serve(order.getId());

        // then
        assertThat(servedOrder.getId()).isEqualTo(order.getId());
        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
      }

      @DisplayName("주문이 접수상태일 경우만 서빙완료 할 수 있다")
      @Test
      void givenNotAcceptOrder_whenServe_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.serve(order.getId()));
      }
    }

    @Nested
    @DisplayName("주문을 완료할 때")
    class WhenOrderComplete {

      @DisplayName("주문 ID를 입력받아 주문완료 상태로 변경할 수 있다.")
      @Test
      void givenValidOrder_whenComplete_thenOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when
        Order servedOrder = orderService.complete(order.getId());

        // then
        assertThat(servedOrder.getId()).isEqualTo(order.getId());
        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
      }

      @DisplayName("매장식사 주문인 경우 제공완료 상태가 아니면 주문을 완료할 수 없다.")
      @Test
      void givenEatInOrder_whenComplete_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.complete(order.getId()));
      }

      @DisplayName("매장식사 주문인 경우 주문테이블에 포함된 주문 건이 모두 완료된 상태라면 주문테이블을 정리한다.")
      @Test
      void givenValidEatInOrder_whenComplete_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestEatInOrder);
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when
        Order completedOrder = orderService.complete(order.getId());

        // then
        assertThat(completedOrder.getId()).isEqualTo(order.getId());
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(completedOrder.getOrderTable().getNumberOfGuests()).isZero();
        assertThat(completedOrder.getOrderTable().isOccupied()).isFalse();
      }
    }

    @Nested
    @DisplayName("주문을 조회할 때")
    class WhenOrderFind {

      @DisplayName("생성된 주문 목록을 조회할 수 있다.")
      @Test
      void givenOrders_whenFindAll_thenReturnOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order requestEatInOrder = OrderFixtures.createRequestEatInOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        orderService.create(requestEatInOrder);
        orderService.create(requestEatInOrder);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(Order::getType).contains(OrderType.EAT_IN);
        assertThat(orders).extracting(Order::getStatus).contains(OrderStatus.WAITING);
      }
    }

  }

  @Nested
  @DisplayName("포장 주문")
  class TakeoutOrder {

    @Nested
    @DisplayName("주문을 생성할 때")
    class WhenOrderCreate {

      @DisplayName("유효한 포장 주문정보를 입력하면 생성된 주문을 반환한다")
      @Test
      void givenValidTakeOutOrder_whenCreate_thenReturnOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        Order requestTakeoutOrder = OrderFixtures.createRequestTakeout(
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        // when
        Order createdOrder = orderService.create(requestTakeoutOrder);

        // then
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getType()).isEqualTo(OrderType.TAKEOUT);
        assertThat(createdOrder.getOrderLineItems()).hasSize(1);
        assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenu)
            .contains(menu);
      }

      @DisplayName("주문상품 메뉴가 진열 중이 아니면 주문을 생성할 수 없다.")
      @Test
      void givenHiddenMenu_whenCreate_thenIllegalStateException() {
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), false));

        Order requestTakeoutOrder = OrderFixtures.createRequestTakeout(
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.create(requestTakeoutOrder));
      }

      @DisplayName("주문상품 가격과 메뉴가격이 일치하지 않으면 주문을 생성할 수 없다.")
      @Test
      void givenNotValidMenuPrice_whenCreate_thenIllegalArgumentException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        Order requestTakeoutOrder = OrderFixtures.createRequestTakeout(
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(24000), 3)));

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(requestTakeoutOrder));
      }
    }

    @Nested
    @DisplayName("주문을 완료할 때")
    class WhenOrderComplete {

      @DisplayName("포장 주문인 경우 제공완료 상태가 아니면 주문을 완료할 수 없다.")
      @Test
      void givenTakeOutOrder_whenComplete_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        Order requestTakeoutOrder = OrderFixtures.createRequestTakeout(
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)));

        Order order = orderService.create(requestTakeoutOrder);

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.complete(order.getId()));
      }

    }

  }

  @Nested
  @DisplayName("배달 주문")
  class DeliveryOrder {

    @Nested
    @DisplayName("주문을 생성할 때")
    class WhenOrderCreate {

      @DisplayName("유효한 배달 주문정보를 입력하면 생성된 주문을 반환한다")
      @Test
      void givenValidDeliveryOrder_whenCreate_thenReturnOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
            "서울시 강남구"
        );

        // when
        Order createdOrder = orderService.create(creationRequestOrder);

        // then
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(createdOrder.getDeliveryAddress()).isEqualTo("서울시 강남구");
        assertThat(createdOrder.getOrderLineItems()).hasSize(1);
        assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenu)
            .contains(menu);
      }

      @DisplayName("주문상품수량이 0개 보다 작을 수 없다.")
      @Test
      void givenNotValidQuantityDelivery_whenCreate_thenIllegalArgumentException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), -1)),
            "서울시 강남구"
        );

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(creationRequestOrder));
      }

      @DisplayName("배달주소가 비어있을 수 없다.")
      @NullAndEmptySource
      @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
      void givenEmptyDeliveryAddress_whenCreate_thenIllegalArgumentException(
          String deliveryAddress) {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
            null
        );

        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderService.create(creationRequestOrder));
      }

      @Nested
      @DisplayName("주문을 배송할 때")
      class WhenOrderDelivery {

        @DisplayName("주문 ID를 입력받아 주문건을 배송 중 상태로 변경한다.")
        @Test
        void givenValidDeliveryOrder_whenStartDelivery_thenOrder() {
          // given
          Menu menu = menuRepository.save(
              OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

          OrderTable orderTable = orderTableRepository.save(
              OrderFixtures.createRequestOrderTable("1번", 5, true));

          Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
              orderTable.getId(),
              List.of(
                  OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
              "서울시 강남구"
          );

          Order order = orderService.create(creationRequestOrder);
          orderService.accept(order.getId());
          orderService.serve(order.getId());

          // when
          Order servedOrder = orderService.startDelivery(order.getId());

          // then
          assertThat(servedOrder.getId()).isEqualTo(order.getId());
          assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("주문타입이 배달인 경우에만 진행할 수 있다.")
        @Test
        void givenNotDeliveryOrder_whenStartDelivery_thenIllegalStateException() {
          // given
          Menu menu = menuRepository.save(
              OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

          OrderTable orderTable = orderTableRepository.save(
              OrderFixtures.createRequestOrderTable("1번", 5, true));

          Order creationRequestOrder = OrderFixtures.createRequestEatInOrder(
              orderTable.getId(),
              List.of(
                  OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3))
          );

          Order order = orderService.create(creationRequestOrder);

          // when & then
          assertThatIllegalStateException()
              .isThrownBy(() -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("배달 주문이 준비되지 않은 경우에 배송을 시작할 수 없다.")
        @Test
        void givenServedOrder_whenStartDelivery_thenIllegalStateException() {
          // given
          Menu menu = menuRepository.save(
              OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

          OrderTable orderTable = orderTableRepository.save(
              OrderFixtures.createRequestOrderTable("1번", 5, true));

          Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
              orderTable.getId(),
              List.of(
                  OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
              "서울시 강남구"
          );

          Order order = orderService.create(creationRequestOrder);

          // when & then
          assertThatIllegalStateException()
              .isThrownBy(() -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("주문 ID를 입력받아 주문건을 배송완료 상태로 변경한다.")
        @Test
        void givenValidOrder_whenCompleteDelivery_thenOrder() {
          // given
          Menu menu = menuRepository.save(
              OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

          OrderTable orderTable = orderTableRepository.save(
              OrderFixtures.createRequestOrderTable("1번", 5, true));

          Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
              orderTable.getId(),
              List.of(
                  OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
              "서울시 강남구"
          );

          Order order = orderService.create(creationRequestOrder);
          orderService.accept(order.getId());
          orderService.serve(order.getId());
          orderService.startDelivery(order.getId());

          // when
          Order servedOrder = orderService.completeDelivery(order.getId());

          // then
          assertThat(servedOrder.getId()).isEqualTo(order.getId());
          assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("주문이 배송 중인 경우에만 배송완료 상태로 변경 가능하다.")
        @Test
        void givenNotDeliveringOrder_whenStartDelivery_thenIllegalStateException() {
          // given
          Menu menu = menuRepository.save(
              OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

          OrderTable orderTable = orderTableRepository.save(
              OrderFixtures.createRequestOrderTable("1번", 5, true));

          Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
              orderTable.getId(),
              List.of(
                  OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
              "서울시 강남구"
          );

          Order order = orderService.create(creationRequestOrder);
          orderService.accept(order.getId());
          orderService.serve(order.getId());

          // when & then
          assertThatIllegalStateException()
              .isThrownBy(() -> orderService.completeDelivery(order.getId()));
        }
      }
    }

    @Nested
    @DisplayName("주문을 배송할 때")
    class WhenOrderRequestDelivery {

      @DisplayName("라이더스에게 주문ID, 메뉴가격, 배송지 정보를 통해 배달을 요청한다.")
      @Test
      void givenDelivery_whenRequestDelivery_thenOrder() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
            "서울시 강남구"
        );

        Order order = orderService.create(creationRequestOrder);

        // when
        Order acceptedOrder = orderService.accept(order.getId());

        // then
        assertThat(acceptedOrder.getId()).isEqualTo(order.getId());
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
      }
    }

    @Nested
    @DisplayName("주문을 완료할 때")
    class WhenOrderComplete {

      @DisplayName("배송주문인 경우 배송완료 상태가 아니면 주문을 완료할 수 없다.")
      @Test
      void givenNotDelivered_whenComplete_thenIllegalStateException() {
        // given
        Menu menu = menuRepository.save(
            OrderFixtures.createRequestMenu("후라이드치킨", BigDecimal.valueOf(23000), true));

        OrderTable orderTable = orderTableRepository.save(
            OrderFixtures.createRequestOrderTable("1번", 5, true));

        Order creationRequestOrder = OrderFixtures.createRequestDeliveryOrder(
            orderTable.getId(),
            List.of(
                OrderFixtures.createRequestOrderLineItem(menu.getId(), BigDecimal.valueOf(23000), 3)),
            "서울시 강남구"
        );

        Order order = orderService.create(creationRequestOrder);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());

        // when & then
        assertThatIllegalStateException()
            .isThrownBy(() -> orderService.complete(order.getId()));
      }
    }
  }
}
