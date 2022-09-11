package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.RiderAgencyClient;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderLineItemFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.fake.FakeRiderAgencyClient;
import kitchenpos.fixture.fake.InMemoryMenuRepository;
import kitchenpos.fixture.fake.InMemoryOrderRepository;
import kitchenpos.fixture.fake.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceWithFakeTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private RiderAgencyClient riderAgencyClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        riderAgencyClient = new FakeRiderAgencyClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, riderAgencyClient);
    }

    @DisplayName("주문을 등록")
    @Nested
    class CreateTest {

        @DisplayName("주문타입은 null 일 수 없다.")
        @Test
        void null_type() {
            // given
            final Order request = OrderFixture.createRequest(null);

            // then
            assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "주문 내역은 비어있을 수 없다. orderLineItems={0}")
        @NullAndEmptySource
        void null_or_empty_orderLineItems(List<OrderLineItem> orderLineItems) {
            // given
            final Order request = OrderFixture.createRequest(OrderType.EAT_IN, orderLineItems);

            // then
            assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 내역의 메뉴들은 모두 등록된 메뉴여야 한다.")
        @Test
        void contain_not_created_menu() {
            // given
            final Order request = OrderFixture.createRequest(OrderType.EAT_IN);

            // then
            assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 내역의 메뉴들은 모두 개시된 메뉴여야 한다.")
        @Test
        void contain_hidden_menu() {
            // given
            UUID menuId = menuRepository.save(MenuFixture.create(false)).getId();
            final Order request = OrderFixture.createRequest(OrderType.EAT_IN, List.of(OrderLineItemFixture.createRequest(menuId)));

            // then
            assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 내역의 가격은 메뉴에 있는 가격과 동일해야 한다.")
        @Test
        void same_menuPrice_and_orderLineItemsPrice() {
            // given
            final UUID menuId = menuRepository.save(MenuFixture.create(20_000L)).getId();
            final Order request = OrderFixture.createRequest(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.createRequest(menuId, 25_000L))
            );

            // then
            assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장 식사 주문")
        @Nested
        class EatIn {

            @DisplayName("등록 성공")
            @Test
            void create() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final UUID orderTableId = orderTableRepository.save(OrderTableFixture.createUsedTable()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.EAT_IN,
                        orderTableId,
                        List.of(OrderLineItemFixture.createRequest(menuId))
                );

                // when
                final Order result = orderService.create(request);

                // then
                assertAll(() -> {
                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getOrderDateTime()).isNotNull();
                    assertThat(result.getOrderLineItems().size()).isNotZero();
                    assertThat(result.getOrderTable()).isNotNull();
                });
            }

            @DisplayName("사용중인 테이블이 지정되어 있어야 한다.")
            @Test
            void not_occupied_table() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final UUID orderTableId = orderTableRepository.save(OrderTableFixture.create(false)).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.EAT_IN,
                        orderTableId,
                        List.of(OrderLineItemFixture.createRequest(menuId))
                );

                // then
                assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class Takeout {

            @DisplayName("등록 성공")
            @Test
            void create() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.TAKEOUT,
                        List.of(OrderLineItemFixture.createRequest(menuId))
                );

                // when
                final Order result = orderService.create(request);

                // then
                assertAll(() -> {
                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getOrderDateTime()).isNotNull();
                    assertThat(result.getOrderLineItems().size()).isNotZero();
                });
            }

            @DisplayName("주문 내역의 메뉴 개수는 0개 이상이여야 한다.")
            @Test
            void negative_menu_quantity() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.TAKEOUT,
                        List.of(OrderLineItemFixture.createRequest(menuId, 15_000L, -1L))
                );

                // then
                assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("배달 주문")
        @Nested
        class Delivery {

            @DisplayName("등록 성공")
            @Test
            void create() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.DELIVERY,
                        List.of(OrderLineItemFixture.createRequest(menuId))
                );

                // when
                final Order result = orderService.create(request);

                // then
                assertAll(() -> {
                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
                    assertThat(result.getOrderDateTime()).isNotNull();
                    assertThat(result.getOrderLineItems().size()).isNotZero();
                    assertThat(result.getDeliveryAddress()).isNotNull();
                });
            }

            @DisplayName("주문 내역의 메뉴 개수는 0개 이상이여야 한다.")
            @Test
            void negative_menu_quantity() {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.DELIVERY,
                        List.of(OrderLineItemFixture.createRequest(menuId, 15_000L, -1L))
                );

                // then
                assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
            }

            @ParameterizedTest(name = "주소지가 비어있을 수 없다. deliveryAddress={0}")
            @NullAndEmptySource
            void null_or_empty_address(String deliveryAddress) {
                // given
                final UUID menuId = menuRepository.save(MenuFixture.createDefault()).getId();
                final Order request = OrderFixture.createRequest(
                        OrderType.DELIVERY,
                        deliveryAddress,
                        List.of(OrderLineItemFixture.createRequest(menuId))
                );

                // then
                assertThatThrownBy(() -> orderService.create(request)).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("주문을 승인한다.")
    @Nested
    class AcceptTest {

        @ParameterizedTest(name = "승인 성공 type={0}")
        @EnumSource(value = OrderType.class)
        void accept(OrderType type) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(type, OrderStatus.WAITING));

            // when
            Order result = orderService.accept(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @ParameterizedTest(name = "주문 상태가 대기 상태이여야한다. status={0}")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAITING")
        void order_status_not_waiting(OrderStatus status) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, status));

            // then
            assertThatThrownBy(() -> orderService.accept(order.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문한 제품을 서빙한다.")
    @Nested
    class ServeTest {

        @ParameterizedTest(name = "서빙 성공 type={0}")
        @EnumSource(value = OrderType.class)
        void serve(OrderType type) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(type, OrderStatus.ACCEPTED));

            // when
            Order result = orderService.serve(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @ParameterizedTest(name = "주문 상태가 승인 상태이여야한다. status={0}")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACCEPTED")
        void order_status_not_accepted(OrderStatus status) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, status));

            // then
            assertThatThrownBy(() -> orderService.serve(order.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문한 제품을 배송한다.")
    @Nested
    class StartDeliveryTest {

        @DisplayName("배송 성공")
        @Test
        void startDelivery() {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, OrderStatus.SERVED));

            // when
            Order result = orderService.startDelivery(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @ParameterizedTest(name = "주문의 타입이 배송이여야 한다 type={0}")
        @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERY")
        void order_type_not_delivery(OrderType type) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(type, OrderStatus.SERVED));

            // then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId())).isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "주문 상태가 서빙완료 상태이여야한다. status={0}")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
        void order_status_not_served(OrderStatus status) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, status));

            // then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문 배송을 완료한다.")
    @Nested
    class CompleteDeliveryTest {

        @DisplayName("배송 완료 성공")
        @Test
        void completedDelivery() {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, OrderStatus.DELIVERING));

            // when
            Order result = orderService.completeDelivery(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @ParameterizedTest(name = "주문 상태가 배송중 상태이여야한다. status={0}")
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERING")
        void order_status_not_delivering(OrderStatus status) {
            // given
            final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, status));

            // then
            assertThatThrownBy(() -> orderService.completeDelivery(order.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문을 완료한다.")
    @Nested
    class CompleteTest {

        @DisplayName("매장식사")
        @Nested
        class EatIn {

            @DisplayName("완료 성공")
            @Test
            void completed() {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, OrderStatus.SERVED));

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertAll(() -> {
                    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(result.getOrderTable().getNumberOfGuests()).isZero();
                    assertThat(result.getOrderTable().isOccupied()).isFalse();
                });
            }

            @ParameterizedTest(name = "주문 상태가 서빙완료 상태이여야한다. status={0}")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
            void order_status_not_served(OrderStatus status) {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, status));

                // then
                assertThatThrownBy(() -> orderService.complete(order.getId())).isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("포장")
        @Nested
        class Takeout {

            @DisplayName("완료 성공")
            @Test
            void completed() {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.TAKEOUT, OrderStatus.SERVED));

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @ParameterizedTest(name = "주문 상태가 서빙완료 상태이여야한다. status={0}")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
            void order_status_not_served(OrderStatus status) {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.TAKEOUT, status));

                // then
                assertThatThrownBy(() -> orderService.complete(order.getId())).isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("배달")
        @Nested
        class Delivery {

            @DisplayName("완료 성공")
            @Test
            void completed() {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, OrderStatus.DELIVERED));

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @ParameterizedTest(name = "주문 상태가 배송완료 상태이여야한다. status={0}")
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERED")
            void order_status_not_served(OrderStatus status) {
                // given
                final Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, status));

                // then
                assertThatThrownBy(() -> orderService.complete(order.getId())).isInstanceOf(IllegalStateException.class);
            }
        }
    }
}
