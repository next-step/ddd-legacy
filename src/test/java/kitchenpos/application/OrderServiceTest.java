package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.repository.MenuFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        menuRepository = new MenuFakeRepository();

        sut = new OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );
    }

    @Nested
    class 주문_등록 {

        @DisplayName("주문을 신규 등록한다")
        @Test
        void testCreate() {
            // given
            Menu menu = menuRepository.save(MenuFixture.create());
            Order order = OrderFixture.createDelivery(menu);

            given(orderRepository.save(any(Order.class))).willReturn(order);

            // when
            Order actual = sut.create(order);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        // 튜플은 2개의 원소가 짝을 이뤄서 하나의 값을 가지는?  그거 아님?

        @Nested
        class 주문_유형별 {

            @DisplayName("배달 주문은 배달장소가 공백이면 신규 등록할 수 없다")
            @Test
            void testCreateWhenDeliveryOrderHasNotDelivery() {
                // given
                Menu menu = menuRepository.save(MenuFixture.create());
                Order order = OrderFixture.createDelivery("", menu);

                // when // then
                assertThatThrownBy(() -> sut.create(order))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("매장식사는 테이블이 점유된 상태여야만 주문을 생성할 수 있다")
            @Test
            void testCreateEatInOrderWhenNotOccupiedOrderTable() {
                // given
                OrderTable orderTable = OrderTableFixture.createEmpty();
                Menu menu = menuRepository.save(MenuFixture.create());
                Order order = OrderFixture.createEatIn(orderTable, menu);

                given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

                // when // then
                assertThatThrownBy(() -> sut.create(order))
                    .isExactlyInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("숨겨진 메뉴로는 주문을 생성할 수 없다")
        @Test
        void testCreateWhenOrderContainHiddenMenu() {
            // given
            Menu hideMenu = menuRepository.save(MenuFixture.create(false));
            Order order = OrderFixture.createDelivery(hideMenu);

            // when // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문항목의 메뉴의 가격 x 수량이 가격과 일치하지 않으면 주문을 생성할 수 없다")
        @Test
        void testCreateWhenOrderLineItemMenuPriceAndMenuPriceIsNotSame() {
            // given
            Menu menu = menuRepository.save(MenuFixture.create(15_000));
            Menu strangeMenu = MenuFixture.create(5_000);
            strangeMenu.setId(menu.getId());
            Order order = OrderFixture.createDelivery(strangeMenu);

            // when // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문항목이 없으면 주문을 생성할 수 없다")
        @Test
        void testCreateWhenOrderLineItemIsEmpty() {
            // given
            Order order = OrderFixture.create(Collections.emptyList());

            // when // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장식사는 주문은 존재하지 않는 테이블에서는 생성할 수 없다")
        @Test
        void testCreateEatInOrderWhenNotExistOrderTable() {
            // given
            Menu menu = menuRepository.save(MenuFixture.create());
            Order order = OrderFixture.createEatIn(OrderTableFixture.create(), menu);

            given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class 주문_수락 {

        @DisplayName("주문을 수락한다")
        @Test
        void testAccept() {
            // given
            Order order = OrderFixture.createDelivery();
            Menu menu = MenuFixture.create();

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.accept(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("대기중인 주문이 아니라면 주문을 수락할 수 없다")
        @ParameterizedTest
        @MethodSource
        void testAcceptWhenOrderStatusIsNotAccepted(OrderStatus orderStatus) {
            // given
            Order order = OrderFixture.createDelivery(orderStatus);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.accept(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        private Stream<OrderStatus> testAcceptWhenOrderStatusIsNotAccepted() {
            return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
            );
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class 주문_서빙 {

        @DisplayName("주문을 서빙한다")
        @Test
        void testServe() {
            // given
            Order order = OrderFixture.createDelivery(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.serve(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("수락된 주문이 아니면 주문을 서빙할 수 없다")
        @ParameterizedTest
        @MethodSource
        void testServeWhenOrderStatusIsNotAccepted(OrderStatus orderStatus) {
            // given
            Order order = OrderFixture.createDelivery(orderStatus);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.serve(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        private Stream<OrderStatus> testServeWhenOrderStatusIsNotAccepted() {
            return Stream.of(
                OrderStatus.WAITING,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
            );
        }

        @DisplayName("존재하지 않는 주문은 서빙할 수 없다")
        @Test
        void testServeWhenNotExistOrder() {
            // given
            Order order = OrderFixture.createDelivery(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.serve(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class 주문_배달_시작 {

        @DisplayName("주문을 배달을 시작한다")
        @Test
        void testStartDelivery() {
            // given
            Order order = OrderFixture.createDelivery(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.startDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("배달주문이 아니면 주문의 배달을 시작할 수 없다")
        @Test
        void testStartDeliveryWhenNotDeliveryOrder() {
            // given
            Order order = OrderFixture.createEatIn(OrderTableFixture.create());

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문은 서빙된 상태에서만 배달을 시작할 수 있다")
        @ParameterizedTest
        @MethodSource
        void testStartDeliveryWhenOrderStatusIsNotDelivery(OrderStatus orderStatus) {
            // given
            Order order = OrderFixture.createDelivery(orderStatus);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        private Stream<OrderStatus> testStartDeliveryWhenOrderStatusIsNotDelivery() {
            return Stream.of(
                OrderStatus.WAITING,
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING
            );
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class 주문_배송완료 {

        @DisplayName("주문을 배송완료 처리한다")
        @Test
        void testCompleteDelivery() {
            // given
            Order order = OrderFixture.createDelivery(OrderStatus.DELIVERING);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.completeDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("배달주문이 아니면 주문을 배송완료 처리할 수 없다")
        @Test
        void testCompleteDeliveryWhenNotDeliveryOrder() {
            // given
            Order order = OrderFixture.createEatIn(OrderTableFixture.create());

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달 완료 처리는 배달중인 상태에서만 처리할 수 있다")
        @ParameterizedTest
        @MethodSource
        void testCompleteDeliveryWhenOrderStatusIsNotDelivery(OrderStatus orderStatus) {
            // given
            Order order = OrderFixture.createDelivery(orderStatus);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> sut.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        private Stream<OrderStatus> testCompleteDeliveryWhenOrderStatusIsNotDelivery() {
            return Stream.of(
                OrderStatus.WAITING,
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING
            );
        }
    }

    @Nested
    class 주문_완료 {

        @DisplayName("배달 주문을 완료 처리한다")
        @Test
        void testDeliveryOrderComplete() {
            // given
            Order order = OrderFixture.createDelivery(OrderStatus.DELIVERED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.complete(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("매장식사 주문을 완료 처리한다")
        @Test
        void testEatInOrderComplete() {
            // given
            Order order = OrderFixture.createEatIn(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            Order actual = sut.complete(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    class 주문_조회 {

        @DisplayName("모든 주문을 조회한다")
        @Test
        void testFindAll() {
            // given
            Order deliveryOrder = OrderFixture.createDelivery();
            Order eatInOrder = OrderFixture.createEatIn(OrderTableFixture.create());

            given(orderRepository.findAll()).willReturn(List.of(deliveryOrder, eatInOrder));

            // when
            List<Order> actual = sut.findAll();

            // then
            assertThat(actual).hasSize(2);
            assertThat(actual.get(0)).isEqualTo(deliveryOrder);
            assertThat(actual.get(1)).isEqualTo(eatInOrder);
        }
    }
}
