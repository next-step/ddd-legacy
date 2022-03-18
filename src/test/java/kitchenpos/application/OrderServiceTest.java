package kitchenpos.application;

import static kitchenpos.application.Fixtures.createDeliveryOrder;
import static kitchenpos.application.Fixtures.createEatInOrder;
import static kitchenpos.application.Fixtures.createMenu;
import static kitchenpos.application.Fixtures.createOrder;
import static kitchenpos.application.Fixtures.createOrderLineItem;
import static kitchenpos.application.Fixtures.createOrderTable;
import static kitchenpos.application.Fixtures.createTakeOutOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeOrderRepository;
import kitchenpos.domain.FakeOrderTableRepository;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

@Nested
@DisplayName("Order 는")
class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new FakeOrderRepository();
        menuRepository = new FakeMenuRepository();
        orderTableRepository = new FakeOrderTableRepository();
        kitchenridersClient = new KitchenridersClient();

        orderService = new OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );
    }

    @Nested
    @DisplayName("주문할 수 있다.")
    class 주문할_수_있다 {

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("종류가 없다면 불가능하다.")
        @NullSource
        void 종류가_없다면_불가능하다(OrderType type) {
            // given
            final Order request = createOrder(type, "우리집");

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("메뉴가 포함되지 않으면 불가능하다.")
        @NullAndEmptySource
        void 메뉴가_포함되지_않으면_불가능하다(List<OrderLineItem> orderLineItems) {
            // given
            final Order request = createOrder(OrderType.DELIVERY, "우리집", orderLineItems);

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @Test
        @DisplayName("존재하지 않는 메뉴인 경우 불가능하다.")
        void 존재하지_않는_메뉴인_경우_불가능하다() {
            // given
            final OrderLineItem orderLineItem = createOrderLineItem(
                UUID.randomUUID(),
                -1,
                BigDecimal.TEN
            );

            final Order request = createOrder(OrderType.DELIVERY, "우리집", orderLineItem);

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @ParameterizedTest(name = "{0} 인 경우, 수량: {1}")
        @DisplayName("매장내 식사가 아닌경우 수량은 0보다 작을 수 없다.")
        @CsvSource(value = {"DELIVERY, -1", "TAKEOUT, -2"})
        void 매장내_식사가_아닌_경우_수량은_0보다_작을_수_없다(OrderType orderType, int quantity) {
            // given
            final OrderLineItem orderLineItem = createOrderLineItem(
                UUID.randomUUID(),
                quantity,
                BigDecimal.TEN
            );
            final Order request = createOrder(orderType, "우리집", orderLineItem);

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @Test
        @DisplayName("메뉴가 숨겨진 경우 불가능하다.")
        void 메뉴가_숨겨진_경우_불가능_하다() {
            // given
            final Menu menu = createMenu(BigDecimal.ONE, false, UUID.randomUUID());
            menuRepository.save(menu);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, menu.getPrice());
            final Order request = createOrder(OrderType.DELIVERY, "우리집", orderLineItem);

            // when// then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(request));
        }

        @ParameterizedTest(name = "메뉴 가격: {0}, 추가 하려는 메뉴 가격: {1}")
        @DisplayName("메뉴의 가격이 주문 하려는 메뉴와 다르다면 불가능하다")
        @CsvSource("100, 10")
        void 메뉴의_가격이_주문_하려는_메뉴와_다르다면_불가능하다(BigDecimal menuPrice, BigDecimal orderLineItemPrice) {
            // given
            final Menu menu = createMenu(menuPrice, true, UUID.randomUUID());
            menuRepository.save(menu);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, orderLineItemPrice);
            final Order request = createOrder(OrderType.EAT_IN, UUID.randomUUID(), orderLineItem);

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("배달인 경우 주소가 없을 수 없다")
        @NullAndEmptySource
        void 배달인_경우_주소가_없을_수_없다(String deliveryAddress) {
            // given
            final Menu menu = createMenu(BigDecimal.TEN, true, UUID.randomUUID());
            menuRepository.save(menu);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, menu.getPrice());
            final Order request = createOrder(OrderType.DELIVERY, deliveryAddress, orderLineItem);

            // when// then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
        }

        @Test
        @DisplayName("매장내 식사인 경우 orderTable이 존재하지 않으면 불가능")
        void 매장내_식사인_경우_orderTable이_존재하지_않으면_불가능() {
            // given
            final Menu menu = createMenu(BigDecimal.TEN, true, UUID.randomUUID());
            menuRepository.save(menu);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, menu.getPrice());
            final Order request = createOrder(OrderType.EAT_IN, UUID.randomUUID(), orderLineItem);

            // when// then
            assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("매장내 식사인 경우 orderTable이 비어있다면 불가능")
        void 매장내_식사인_경우_orderTable이_비어있다면_불가능() {
            // given
            final Menu menu = createMenu(BigDecimal.TEN, true, UUID.randomUUID());
            final OrderTable orderTable = createOrderTable(
                UUID.randomUUID(),
                "테이블",
                4,
                true
            );
            menuRepository.save(menu);
            orderTableRepository.save(orderTable);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, menu.getPrice());
            final Order request = createOrder(OrderType.EAT_IN, orderTable.getId(), orderLineItem);

            // when// then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(request));
        }

        @Test
        @DisplayName("매장내 식사인 경우")
        void 매장내_식사인_경우_주문() {
            // given
            final Menu menu = createMenu(BigDecimal.TEN, true, UUID.randomUUID());
            final OrderTable orderTable = createOrderTable(
                UUID.randomUUID(),
                "테이블",
                4,
                false
            );
            menuRepository.save(menu);
            orderTableRepository.save(orderTable);

            final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, menu.getPrice());
            final Order request = createOrder(OrderType.EAT_IN, orderTable.getId(), orderLineItem);

            // when// then

            final Order expected = orderService.create(request);

            assertAll(
                () -> assertThat(expected.getId()).isNotNull(),
                () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(expected.getType()).isEqualTo(OrderType.EAT_IN)
            );
        }
    }

    @Nested
    @DisplayName("수락할 수 있다.")
    class 수락할_수_있다 {

        @Test
        @DisplayName("존재하지 않는 경우 수락할 수 없다")
        void 존재하지_않는_경우_수락할_수_없다() {
            // given

            // when // then
            assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("대기 상태가 아니면 수락할 수 없다")
        @EnumSource(
            value = OrderStatus.class,
            names = "WAITING",
            mode = Mode.EXCLUDE
        )
        void 대기_상태가_아니면_수락할_수_없다(OrderStatus status) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createOrder(
                orderId,
                OrderType.DELIVERY,
                status,
                LocalDateTime.now(),
                "",
                new OrderTable()
            );
            orderRepository.save(order);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.accept(orderId));
        }

        @DisplayName("대기 상태라면")
        @ParameterizedTest(name = "{0}인 경우")
        @EnumSource(value = OrderType.class)
        void 대기_상태라면(OrderType orderType) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createOrder(
                orderId,
                orderType,
                OrderStatus.WAITING,
                LocalDateTime.now(),
                "",
                new OrderTable(),
                createOrderLineItem(
                    createMenu(BigDecimal.ONE, true, UUID.randomUUID()),
                    2,
                    BigDecimal.ONE
                )
            );
            orderRepository.save(order);

            // when
            final Order expected = orderService.accept(orderId);

            // then
            assertAll(
                () -> assertThat(expected.getId()).isEqualTo(orderId),
                () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }
    }

    @Nested
    @DisplayName("전달될 수 있다")
    class 전달될_수_있다 {

        @ParameterizedTest(name = "{0}인 경우")
        @DisplayName("수락 상태가 아니라면 전달될 수 없다")
        @EnumSource(
            value = OrderStatus.class,
            names = "ACCEPTED",
            mode = Mode.EXCLUDE
        )
        void 수락_상태가_아니라면_전달될_수_없다(OrderStatus status) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createOrder(
                orderId,
                OrderType.DELIVERY,
                status,
                LocalDateTime.now(),
                "",
                new OrderTable()
            );

            orderRepository.save(order);
            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.serve(orderId));
        }

        @Test
        @DisplayName("수락 상태라면 전달 가능하다")
        void 수락_상태라면_전달_가능하다() {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createOrder(
                orderId,
                OrderType.DELIVERY,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                "",
                new OrderTable()
            );
            orderRepository.save(order);
            // when
            final Order expected = orderService.serve(orderId);

            // then
            assertAll(
                () -> assertThat(expected.getId()).isEqualTo(orderId),
                () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.SERVED)
            );
        }
    }

    @Nested
    @DisplayName("배달이고 전달되었다면 배송을 시작할 수 있다.")
    class 배달이이고_전달되었다면_배송을_시작할_수_있다 {

        @ParameterizedTest(name = "{0}인 경우")
        @DisplayName("배달이 아니라면 불가능하다")
        @EnumSource(
            value = OrderType.class,
            names = "DELIVERY",
            mode = Mode.EXCLUDE
        )
        void 배달이_아니라면_불가능하다(OrderType type) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createOrder(
                orderId,
                type,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                "",
                new OrderTable()
            );
            orderRepository.save(order);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(orderId));
        }

        @ParameterizedTest(name = "{0}인 경우")
        @DisplayName("전달된 상태가 아니라면 불가능하다")
        @EnumSource(
            value = OrderStatus.class,
            names = "SERVED",
            mode = Mode.EXCLUDE
        )
        void 전달된_상태가_아니라면_불가능하다(OrderStatus status) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createDeliveryOrder(orderId, status, LocalDateTime.now(), "우리집");
            orderRepository.save(order);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(orderId));
        }

        @Test
        @DisplayName("배달이고 전달되었다면 가능하다")
        void 배달이고_전달되었다면_가능하다() {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createDeliveryOrder(
                orderId,
                OrderStatus.SERVED,
                LocalDateTime.now(),
                "우리집"
            );
            orderRepository.save(order);

            // when
            final Order expected = orderService.startDelivery(orderId);

            // then
            assertAll(
                () -> assertThat(expected.getId()).isEqualTo(orderId),
                () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.DELIVERING)
            );
        }
    }
    
    @Nested
    @DisplayName("배달중이라면 배달 완료할 수 있다")
    class 배달중이라면_배달_완료할_수_있다 {

        @ParameterizedTest(name = "{0}인 경우")
        @DisplayName("배달중이 아니라면 불가능하다")
        @EnumSource(
            value = OrderStatus.class,
            names = "DELIVERING",
            mode = Mode.EXCLUDE
        )
        void 배달중이_아니라면_불가능하다(OrderStatus status) {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createDeliveryOrder(
                orderId,
                status,
                LocalDateTime.now(),
                "우리집"
            );

            orderRepository.save(order);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderService.completeDelivery(orderId));
        }

        @Test
        @DisplayName("배달중이라면 가능하다.")
        void 배달중이라면_가능하다() {
            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = createDeliveryOrder(
                orderId,
                OrderStatus.DELIVERING,
                LocalDateTime.now(),
                "우리집"
            );

            orderRepository.save(order);

            // when
            final Order expected = orderService.completeDelivery(orderId);

            // then
            assertAll(
                () -> assertThat(expected.getId()).isEqualTo(orderId),
                () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.DELIVERED)
            );
        }
    }

    @Nested
    @DisplayName("완료할 수 있다")
    class 완료할_수_있다 {

        @Nested
        @DisplayName("배달인 경우")
        class 배달인_경우 {

            @ParameterizedTest(name = "{0}인 경우")
            @DisplayName("배달 완료가 아니라면 불가능하다")
            @EnumSource(
                value = OrderStatus.class,
                names = "DELIVERED",
                mode = Mode.EXCLUDE
            )
            void 배달_완료가_아니라면_불가능하다(OrderStatus orderStatus) {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createDeliveryOrder(
                    orderId,
                    orderStatus,
                    LocalDateTime.now(),
                    "우리집"
                );
                orderRepository.save(order);

                // when // then
                assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(orderId));
            }

            @Test
            @DisplayName("배달 완료라면 가능하다")
            void 배달_완료라면_가능하다() {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createDeliveryOrder(
                    orderId,
                    OrderStatus.DELIVERED,
                    LocalDateTime.now(),
                    "우리집"
                );
                orderRepository.save(order);

                // when
                final Order expected = orderService.complete(orderId);

                // then
                assertAll(
                    () -> assertThat(expected.getId()).isEqualTo(orderId),
                    () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }

        @Nested
        @DisplayName("테이크 아웃인 경우")
        class 테이크_아웃인_경우 {

            @ParameterizedTest(name = "{0}인 경우")
            @DisplayName("전달된 경우가 아니라면 불가능하다")
            @EnumSource(
                value = OrderStatus.class,
                names = "SERVED",
                mode = Mode.EXCLUDE
            )
            void 전달된_경우가_아니라면_불가능하다(OrderStatus orderStatus) {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createTakeOutOrder(
                    orderId,
                    orderStatus,
                    LocalDateTime.now()
                );
                orderRepository.save(order);

                // when // then
                assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(orderId));
            }

            @Test
            @DisplayName("전달된 경우 가능하다")
            void 전달된_경우_가능하다() {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createTakeOutOrder(
                    orderId,
                    OrderStatus.SERVED,
                    LocalDateTime.now()
                );
                orderRepository.save(order);

                // when
                final Order expected = orderService.complete(orderId);

                // then
                assertAll(
                    () -> assertThat(expected.getId()).isEqualTo(orderId),
                    () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }

        @Nested
        @DisplayName("매장내 식사인 경우")
        class 매장내_식사인_경우 {

            @ParameterizedTest(name = "{0}인 경우")
            @DisplayName("전달된 경우가 아니라면 불가능하다")
            @EnumSource(
                value = OrderStatus.class,
                names = "SERVED",
                mode = Mode.EXCLUDE
            )
            void 전달된_경우가_아니라면_불가능하다(OrderStatus orderStatus) {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createEatInOrder(
                    orderId,
                    orderStatus,
                    LocalDateTime.now(),
                    new OrderTable()
                );
                orderRepository.save(order);

                // when // then
                assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(orderId));
            }

            @Test
            @DisplayName("전달된 경우 가능하다")
            void 전달된_경우_가능하다() {
                // given
                final UUID orderId = UUID.randomUUID();
                final Order order = createEatInOrder(
                    orderId,
                    OrderStatus.SERVED,
                    LocalDateTime.now(),
                    new OrderTable()
                );
                orderRepository.save(order);

                // when
                final Order expected = orderService.complete(orderId);

                // then
                assertAll(
                    () -> assertThat(expected.getId()).isEqualTo(orderId),
                    () -> assertThat(expected.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }
    }
}
