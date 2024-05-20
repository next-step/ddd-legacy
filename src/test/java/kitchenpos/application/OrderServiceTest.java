package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }


    @Nested
    @DisplayName("주문 생성")
    class create {
        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;

        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
            orderTable = OrderTableTestFixture.createOrderTable("1번", true, 2);
            orderTableRepository.save(orderTable);
        }

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when
            Order response = orderService.create(request);

            //then
            assertThat(response.getId()).isNotNull();
        }

        @Test
        @DisplayName("배달주문 성공")
        void successDelivery() {
            //given
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.DELIVERY,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            request.setDeliveryAddress("address-address");

            //when
            Order response = orderService.create(request);

            //then
            assertThat(response.getId()).isNotNull();

        }

        @Test
        @DisplayName("주문타입이 비어있을 수 없다.")
        void canNotEmptyOrderType() {
            //given
            Order request = OrderTestFixture.createOrderRequest(
                    null,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문항목이 비어있을 수 없다.")
        void canNotEmptyOrderLineItems() {
            //given
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(new OrderLineItem()),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

            request.setOrderLineItems(null);
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문항목에는 유효한 메뉴만 있어야 한다.")
        void onlyValidMenuInOrderLineItem() {
            //given
            Product product2 = ProductTestFixture.createProduct("후라이드치킨", 18000L);
            MenuProduct menuProduct2 = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product2);
            Menu menu2 = MenuTestFixture.createMenu("양념치킨", 18000L, true, List.of(menuProduct2));
            OrderLineItem orderLineItem2 = OrderLineItemTestFixture.createOrderLine(1L, 1, menu2);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem, orderLineItem2),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("매장식사가 아닐때 주문항목의 개수가 음수일 수 없다.")
        void canNotMinusOrderLineItemQuantityNumber() {
            //given
            orderLineItem.setQuantity(-1L);
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.DELIVERY,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("표시되지 않은 메뉴는 주문할 수 없다.")
        void canNotOrderHideMenu() {
            //given
            orderLineItem.getMenu().setDisplayed(false);
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("메뉴의 가격과 주문항목의 가격이 같아야 한다.")
        void menuPriceEqualsOrderLineItemPrice() {
            //given
            orderLineItem.setPrice(BigDecimal.valueOf(20000));
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("배달일때 배달주소가 있어야 한다.")
        void mustHaveDeliveryAddress() {
            //given
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.DELIVERY,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("매장식사 일때 주문테이블이 있어야 한다.")
        void mustHaveOrderTableWhenEatIn() {
            //given
            orderTable.setId(UUID.randomUUID());
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("매장식사 일때 주문테이블이 사용중이어야 한다.")
        void mustOccupied() {
            //given
            orderTable.setOccupied(false);
            Order request = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);

            //when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class accept {

        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
            orderTable = OrderTableTestFixture.createOrderTable("1번", true, 2);
            orderTableRepository.save(orderTable);
        }

        @Test
        @DisplayName("주문 상태 주문 수락으로 변경 성공")
        void success() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when
            Order response = orderService.accept(order.getId());

            //then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }


        @Test
        @DisplayName("없는 주문의 상태를 수락으로 바꿀 수 없다.")
        void mustHaveOrder() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태가 주문 대기이어야 한다.")
        void mustWaiting() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 서빙")
    class serve {

        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
            orderTable = OrderTableTestFixture.createOrderTable("1번", true, 2);
            orderTableRepository.save(orderTable);
        }

        @Test
        @DisplayName("주문 상태 서빙으로 변경 성공")
        void success() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.ACCEPTED,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when
            Order response = orderService.serve(order.getId());

            //then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("없는 주문의 상태를 서빙으로 바꿀 수 없다.")
        void mustHaveOrder() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.ACCEPTED,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태가 주문 수락이어야 한다.")
        void mustAccepted() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("배달 시작")
    class startDelivery {

        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
        }

        @Test
        @DisplayName("주문 상태 배달 시작으로 변경 성공")
        void success() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when
            Order response = orderService.startDelivery(order.getId());

            //then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("없는 주문의 상태를 배달시작으로 바꿀 수 없다.")
        void mustHaveOrder() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 유형이 배달이어야 한다.")
        void mustDelivery() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 상태가 주문 수락이어야 한다.")
        void mustAccepted() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.WAITING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("배달 완료")
    class completeDelivery {

        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
        }

        @Test
        @DisplayName("주문 상태 배달 완료로 변경 성공")
        void success() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.DELIVERING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when
            Order response = orderService.completeDelivery(order.getId());

            //then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("없는 주문의 상태를 배달완료로 바꿀 수 없다.")
        void mustHaveOrder() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.DELIVERING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태가 배달 시작 이어야 한다.")
        void mustDelivering() {
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 완료")
    class complete {

        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(menuProduct));
            menuRepository.save(menu);
            orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);
            orderTable = OrderTableTestFixture.createOrderTable("1번", true, 2);
            orderTableRepository.save(orderTable);
        }

        @Test
        @DisplayName("주문 완료로 변경 성공")
        void success(){
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem),
                    orderTable);
            orderRepository.save(order);

            //when
            Order response = orderService.complete(order.getId());

            //then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("없는 주문의 상태를 주문완료로 바꿀 수 없다.")
        void mustHaveOrder(){
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문유형이 배달일때 주문상태는 배달완료여야 한다.")
        void DeliveryAndDelivered(){
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.DELIVERING,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문유형이 포장이거나 매장식사일때 주문상태는 서빙이어야 한다.")
        void TakeOutOrEatInAndServed(){
            //given
            Order order = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.ACCEPTED,
                    LocalDateTime.now(),
                    List.of(orderLineItem));
            orderRepository.save(order);

            //when then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class find {
        @Test
        @DisplayName("모든 주문 조회")
        void findAll() {

            //given
            Order order1 = OrderTestFixture.createOrder(
                    OrderType.DELIVERY,
                    OrderStatus.DELIVERING,
                    LocalDateTime.now(),
                    List.of(new OrderLineItem()));
            Order order2 = OrderTestFixture.createOrder(
                    OrderType.EAT_IN,
                    OrderStatus.ACCEPTED,
                    LocalDateTime.now(),
                    List.of(new OrderLineItem()),
                    new OrderTable());

            orderRepository.save(order1);
            orderRepository.save(order2);

            //when
            List<Order> response = orderService.findAll();

            //then
            assertThat(response).hasSize(2);
            assertThat(response)
                    .filteredOn(Order::getId, order1.getId())
                    .containsExactly(order1);
            assertThat(response)
                    .filteredOn(Order::getId, order2.getId())
                    .containsExactly(order2);
        }
    }

}