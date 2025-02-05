package kitchenpos.application;

import kitchenpos.application.fixture.*;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class OrderServiceTest {

    private static final UUID DISPLAY_MENU_ID = UUID.randomUUID();
    private static final UUID UNDISPLAYED_MENU_ID = UUID.randomUUID();
    private static final UUID ORDER_TABLE_ID = UUID.randomUUID();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderService orderService;


    @BeforeEach
    void setUp() {
        Product product1 = productRepository.save(ProductFixture.createProduct(UUID.randomUUID(), "전시", new BigDecimal(25000)));
        Product product2 = productRepository.save(ProductFixture.createProduct(UUID.randomUUID(), "비전시", new BigDecimal(7000)));

        UUID menuGroupId = UUID.randomUUID();
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.createMenuGroup(menuGroupId, "단품"));

        MenuProduct displayMenuProduct = MenuProductFixture.createMenuProduct(product1, 1);
        menuRepository.save(MenuFixture.createMenu(DISPLAY_MENU_ID, menuGroup, menuGroup.getId(), "전시메뉴", new BigDecimal(25000), true, List.of(displayMenuProduct)));

        MenuProduct unDisplayMenuProduct = MenuProductFixture.createMenuProduct(product2, 1);
        menuRepository.save(MenuFixture.createMenu(UNDISPLAYED_MENU_ID, menuGroup, menuGroup.getId(), "비전시메뉴", new BigDecimal(7000), false, List.of(unDisplayMenuProduct)));
    }

    @DisplayName("주문을 생성할 수 있다")
    @Nested
    class OrderCreator {

        @DisplayName("주문의 종류는 반드시 입력해야 한다")
        @NullSource
        @ParameterizedTest
        void orderType(OrderType status) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
            Order nullOrderTypeRequest = OrderFixture.createOrder(status, List.of(orderLineItem), "", null, null);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderService.create(nullOrderTypeRequest));
        }

        @DisplayName("포장 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
        @Test
        void takeOutOrder() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
            Order orderRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());

            Order orderResult = orderService.create(orderRequest);

            assertAll(
                    () -> assertThat(orderResult.getType()).isEqualTo(OrderType.TAKEOUT),
                    () -> assertThat(orderResult.getOrderLineItems()).isNotNull(),
                    () -> assertThat(orderResult.getOrderLineItems()).hasSize(1),
                    () -> assertThat(orderResult.getDeliveryAddress()).isNull(),
                    () -> assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING)
            );
        }

        @DisplayName("배달 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
        @Test
        void deliveryOrder() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
            Order orderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());

            Order orderResult = orderService.create(orderRequest);

            assertAll(
                    () -> assertThat(orderResult.getType()).isEqualTo(OrderType.DELIVERY),
                    () -> assertThat(orderResult.getOrderLineItems()).isNotNull(),
                    () -> assertThat(orderResult.getOrderLineItems()).hasSize(1),
                    () -> assertThat(orderResult.getDeliveryAddress()).isNotNull(),
                    () -> assertThat(orderResult.getDeliveryAddress()).isEqualTo("경기도 고양시..XX동 XX호"),
                    () -> assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING)
            );
        }

        @DisplayName("매장 내 식사 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
        @Test
        void eatInOrder() {
            OrderTable request = OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4);
            OrderTable orderTable = orderTableRepository.save(request);

            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
            Order orderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

            Order orderResult = orderService.create(orderRequest);

            assertAll(
                    () -> assertThat(orderResult.getType()).isEqualTo(OrderType.EAT_IN),
                    () -> assertThat(orderResult.getOrderLineItems()).isNotNull(),
                    () -> assertThat(orderResult.getOrderLineItems()).hasSize(1),
                    () -> assertThat(orderResult.getDeliveryAddress()).isNull(),
                    () -> assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING)
            );
        }

        @DisplayName("어떤 메뉴를 몇 개 시킬지에 대한 주문 상세 내역이 1개 이상 있어야 한다")
        @ParameterizedTest
        @MethodSource("nullOrEmptyList")
        void notNullOrderLineItems(List<OrderLineItem> orderLineItems) {
            Order takeOutRequest = createTakeOutOrder(orderLineItems, LocalDateTime.now());
            Order deliveryOrderRequest = createDeliveryOrder(orderLineItems, "경기도 고양시..XX동 XX호", LocalDateTime.now());
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInOrderRequest = createEatInOrder(orderLineItems, orderTable.getId(), LocalDateTime.now());

            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(takeOutRequest)),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(deliveryOrderRequest)),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(eatInOrderRequest))
            );
        }

        private static Stream<List> nullOrEmptyList() {
            return Stream.of(null, Collections.emptyList());
        }

        @DisplayName("배달/포장 주문의 경우, 주문한 메뉴의 수량이 0개 이상이어야 한다")
        @Test
        void validateQuantity() {
            int negativeQuantity = -1;
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), negativeQuantity);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());

            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(takeOutRequest)),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(deliveryOrderRequest))
            );
        }

        @DisplayName("메뉴판에 전시하지 않은 메뉴는 주문할 수 없다")
        @Test
        void validateMenuOrder() {
            OrderLineItem undisplayMenuOrderItem = OrderFixture.createOrderLineItem(UNDISPLAYED_MENU_ID, new BigDecimal("25000"), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(undisplayMenuOrderItem), LocalDateTime.now());
            Order deliveryOrderRequest = createDeliveryOrder(List.of(undisplayMenuOrderItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInOrderRequest = createEatInOrder(List.of(undisplayMenuOrderItem), orderTable.getId(), LocalDateTime.now());

            assertAll(
                    () -> assertThatIllegalStateException()
                            .isThrownBy(() -> orderService.create(takeOutRequest)),
                    () -> assertThatIllegalStateException()
                            .isThrownBy(() -> orderService.create(deliveryOrderRequest)),
                    () -> assertThatIllegalStateException()
                            .isThrownBy(() -> orderService.create(eatInOrderRequest))
            );
        }

        @DisplayName("메뉴의 가격과 주문 내역의 가격은 동일해야 한다")
        @Test
        void validateOrderPrice() {
            BigDecimal menuPrice = menuRepository.findById(DISPLAY_MENU_ID).get().getPrice();

            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, menuPrice.add(BigDecimal.ONE), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(takeOutRequest)),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(deliveryOrderRequest)),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> orderService.create(eatInOrderRequest))
            );
        }

        @DisplayName("배달의 경우, 배달 주소가 반드시 입력되어야 하며, 공백만 입력되어서는 안된다")
        @ParameterizedTest
        @NullAndEmptySource
        void notNullOrNotEmpty(String deliveryAddress) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), deliveryAddress, LocalDateTime.now());

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderService.create(deliveryOrderRequest));
        }

        @DisplayName("매장 내 취식일 경우, 고객에게 배정된 주문 테이블이 있어야 한다")
        @Test
        void validateOrderTable() {
            UUID unknownTableId = UUID.randomUUID();
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), unknownTableId, LocalDateTime.now());

            assertThatThrownBy(() -> orderService.create(eatInOrderRequest))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("매장 내 취식일 경우, 주문 테이블의 사용유무가 사용 중이어야 한다")
        @Test
        void validateOrderTableStatus() {
            boolean tableOccupied = false;
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", tableOccupied, 0));
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.create(eatInOrderRequest));
        }
    }


    @DisplayName("주문을 수락할 수 있다")
    @Nested
    class OrderAccepter {
        @DisplayName("대기 중인 주문을 수락한다")
        @Test
        void acceptOrderStatus() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order takeOutOrder = orderService.create(takeOutRequest);

            Order acceptOrder = orderService.accept(takeOutOrder.getId());

            assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문의 상태가 대기(WAITING)일 때 주문 수락이 가능하다")
        @EnumSource(value = OrderStatus.class, names = "WAITING", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void validateAcceptStatus(OrderStatus orderStatus) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order takeOutOrder = orderService.create(takeOutRequest);

            takeOutOrder.setStatus(orderStatus);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.accept(takeOutOrder.getId()));
        }
    }


    @DisplayName("수락된 주문을 토대로 음식을 제공할 수 있다")
    @Nested
    class OrderServer {

        @DisplayName("주문 상태를 음식 제공됨(SERVED)로 변경한다")
        @Test
        void served() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order takeOutOrder = orderService.create(takeOutRequest);
            Order acceptOrder = orderService.accept(takeOutOrder.getId());

            Order servedOrder = orderService.serve(acceptOrder.getId());

            assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("주문의 상태가 주문 수락(ACCEPTED)일 때 음식 제공 가능하다")
        @EnumSource(value = OrderStatus.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void validateServedStatus(OrderStatus orderStatus) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order takeOutOrder = orderService.create(takeOutRequest);

            takeOutOrder.setStatus(orderStatus);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.serve(takeOutOrder.getId()));
        }

    }

    @DisplayName("배달 주문의 경우, 주문 상태를 배달 중으로 변경할 수 있다")
    @Nested
    class DeliveryStarter {

        @DisplayName("배달 주문의 경우 음식이 제공되면 주문 상태를 배달 중(DELIVERING)으로 변경한다")
        @Test
        void startDelivery() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = orderService.create(deliveryOrderRequest);

            order.setStatus(OrderStatus.SERVED);
            Order deliveryStartorder = orderService.startDelivery(order.getId());

            assertThat(deliveryStartorder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("주문 상태가 음식 제공됨(SERVED)인 경우, 배달 시작이 가능하다")
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void validateDeliveryStartStatus(OrderStatus orderStatus) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = orderService.create(deliveryOrderRequest);

            order.setStatus(orderStatus);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.startDelivery(order.getId()));
        }
    }

    @DisplayName("배달 주문의 경우, 주문 상태를 배달 완료로 변경할 수 있다")
    @Nested
    class DeliveryCompleter {

        @DisplayName("배달 완료한 주문 상태를 배달완료(DELIVERED)로 변경한다")
        @Test
        void endDelivery() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = orderService.create(deliveryOrderRequest);

            order.setStatus(OrderStatus.DELIVERING);
            Order deliveryStartorder = orderService.completeDelivery(order.getId());

            assertThat(deliveryStartorder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("주문 상태가 배달 중(DELIVERING)인 경우, 배달완료(DELIVERED)로 변경 가능하다")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = "DELIVERING", mode = EnumSource.Mode.EXCLUDE)
        void validateDeliveryEndStatus(OrderStatus orderStatus) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = orderService.create(deliveryOrderRequest);

            order.setStatus(orderStatus);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.completeDelivery(order.getId()));
        }
    }

    @DisplayName("주문의 상태를 주문 완료로 변경할 수 있다")
    @Nested
    class OrderCompleter {

        @DisplayName("배달 주문의 상태를 주문 완료로 변경한다")
        @Test
        void complete() {
            //given
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = createDeliveredOrder(deliveryOrderRequest);
            //when
            Order complete = orderService.complete(order.getId());
            //then
            assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("배달 주문의 경우, 주문의 상태가 배달 완료(DELIVERED)인 경우만 주문을 완료할 수 있다")
        @EnumSource(value = OrderStatus.class, names = "DELIVERED", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void completeOrderByDelivery(OrderStatus orderStatus) {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XXX호", LocalDateTime.now());
            Order order = orderService.create(deliveryOrderRequest);

            order.setStatus(orderStatus);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(order.getId()));
        }

        @DisplayName("포장 주문의 상태를 주문 완료로 변경한다")
        @Test
        void completeByTakeOut() {
            //given
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
            Order servedOrder = createServedOrder(takeOutRequest);
            //when
            Order complete = orderService.complete(servedOrder.getId());
            //then
            assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("매장 내 취식 주문 상태를 주문 완료로 변경한다")
        @Test
        void completeByEatIn() {
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());
            Order servedOrder = createServedOrder(eatInRequest);
            //when
            Order complete = orderService.complete(servedOrder.getId());
            //then
            assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("포장/매장 내 취식의 경우, 주문의 상태가 음식 제공됨(SERVED)인 경우만 주문을 완료할 수 있다")
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void completeOrderByEatInOrTakeOut(OrderStatus orderStatus) {
            //given
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());

            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());

            Order takeoutOrder = orderService.create(takeOutRequest);
            Order eatInOrder = orderService.create(eatInRequest);
            //when
            takeoutOrder.setStatus(orderStatus);
            eatInOrder.setStatus(orderStatus);
            //then
            assertAll(
                    () -> assertThatIllegalStateException()
                            .isThrownBy(() -> orderService.complete(takeoutOrder.getId())),
                    () -> assertThatIllegalStateException()
                            .isThrownBy(() -> orderService.complete(eatInOrder.getId()))
            );
        }

        @DisplayName("매장 내 식사의 경우, 주문 테이블의 모든 주문이 완료 상태가 아닐 때 테이블은 사용 중이며 손님의 수는 0으로 초기화되지 않는다")
        @Test
        void completeOrderByEatIn() {
            //given
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());
            //주문 상태가 SERVED인 주문1,2 생성
            Order servedOrder1 = createServedOrder(eatInRequest);
            Order servedOrder2 = createServedOrder(eatInRequest);

            //when
            Order complete = orderService.complete(servedOrder1.getId());

            //then
            assertAll(
                    () -> assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(complete.getOrderTable().isOccupied()).isTrue(),
                    () -> assertThat(complete.getOrderTable().getNumberOfGuests()).isNotZero()
            );
        }

        @DisplayName("매장 내 취식의 경우, 주문 테이블에의 모든 주문이 완료되면 테이블의 사용유무를 안함으로 변경하고 고객의 수를 0명으로 변경한다")
        @Test
        void clearOrderTable() {
            //given
            OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
            Order eatInRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

            Order servedOrder1 = createServedOrder(eatInRequest);
            Order servedOrder2 = createServedOrder(eatInRequest);

            //when
            Order complete1 = orderService.complete(servedOrder1.getId());
            Order complete2 = orderService.complete(servedOrder2.getId());

            //then
            assertAll(
                    () -> assertThat(complete1.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(complete1.getOrderTable().isOccupied()).isFalse(),
                    () -> assertThat(complete1.getOrderTable().getNumberOfGuests()).isZero(),

                    () -> assertThat(complete2.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(complete2.getOrderTable().isOccupied()).isFalse(),
                    () -> assertThat(complete2.getOrderTable().getNumberOfGuests()).isZero()
            );
        }
    }

    //region [주문 조회]
    @DisplayName("모든 주문들을 조회할 수 있다")
    @Test
    void findAll() {
        //given
        OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable(UUID.randomUUID(), "1번테이블", true, 4));

        Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
        Order deliveryRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());
        Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

        Order takeOutOrder = orderService.create(takeOutRequest);
        Order deliveryOrder = orderService.create(deliveryRequest);
        Order eatInOrder = orderService.create(eatInOrderRequest);
        //when
        List<Order> orders = orderService.findAll();
        //then
        assertThat(orders).hasSize(3);
    }
    //endregion


    private Order createTakeOutOrder(List<OrderLineItem> orderLineItems, LocalDateTime orderDateTime) {
        return OrderFixture.createOrder(OrderType.TAKEOUT, orderLineItems, null, null, orderDateTime);
    }

    private Order createDeliveryOrder(List<OrderLineItem> orderLineItems, String deliveryAddress, LocalDateTime orderDateTime) {
        return OrderFixture.createOrder(OrderType.DELIVERY, orderLineItems, deliveryAddress, null, orderDateTime);
    }

    private Order createEatInOrder(List<OrderLineItem> orderLineItems, UUID orderTableId, LocalDateTime orderDateTime) {
        return OrderFixture.createOrder(OrderType.EAT_IN, orderLineItems, null, orderTableId, orderDateTime);
    }

    private Order createDeliveredOrder(Order deliveryOrder) {
        Order order = orderService.create(deliveryOrder);
        Order accept = orderService.accept(order.getId());
        Order serve = orderService.serve(accept.getId());
        Order startDelivery = orderService.startDelivery(serve.getId());
        return orderService.completeDelivery(startDelivery.getId());
    }

    private Order createServedOrder(Order orderRequest) {
        Order takeOutOrder = orderService.create(orderRequest);
        Order accept = orderService.accept(takeOutOrder.getId());
        return orderService.serve(accept.getId());
    }
}
