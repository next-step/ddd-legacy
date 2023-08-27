package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderFixture.automaticallyInitializeOrder;
import static kitchenpos.fixture.OrderFixture.automaticallyInitializeOrderByType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final int FIRST_ELEMENT = 0;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        sut = new OrderService(
                orderRepository,
                menuRepository,
                orderTableRepository,
                kitchenridersClient
        );
    }

    @DisplayName("배달 또는 포장 주문인 경우에는, 주문한 품목의 수량이 반드시 0개 이상이어야 한다")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    @ParameterizedTest
    void aboveZeroQuantityForDeliveryAndTakeout(final OrderType orderType) {
        // given
        final Order order = automaticallyInitializeOrderByType(orderType);
        setUpStubsForCreatingOrder(order);

        order.getOrderLineItems().get(0).setQuantity(-1);

        // expected
        assertThatThrownBy(() -> sut.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("대기 중인 주문을 수락한다")
    @EnumSource(OrderType.class)
    @ParameterizedTest
    void acceptWaitingOrder(final OrderType orderType) {
        // given
        final Order order = automaticallyInitializeOrderByType(orderType);
        setUpStubsForAcceptingOrder(order);

        // when
        final Order result = sut.accept(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("배달 주문을 수락하면 라이더스에 배달 정보를 전달한다")
    @Test
    void giveDeliveryInfoToKitchenRiders() {
        // given
        final Order order = automaticallyInitializeOrderByType(OrderType.DELIVERY);
        setUpStubsForAcceptingOrder(order);

        // when
        final Order result = sut.accept(order.getId());

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(kitchenridersClient, times(1)).requestDelivery(any(), any(), any());
        });
    }

    @DisplayName("수락된 주문을 서빙한다")
    @EnumSource(OrderType.class)
    @ParameterizedTest
    void serveOrder(final OrderType orderType) {
        // given
        final Order order = automaticallyInitializeOrder(orderType, OrderStatus.ACCEPTED);
        setUpStubsForServingOrder(order);

        // when
        final Order result = sut.serve(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달을 시작한다")
    @Test
    void startDelivery() {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, OrderStatus.SERVED);
        setUpStubsForStartDeliveryOrder(order);

        // when
        final Order result = sut.startDelivery(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문이 아니면 배달을 시작할 수 없다")
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
    @ParameterizedTest
    void throwExceptionNotDeliveryOrderWhenStartingDelivery(final OrderType orderType) {
        // given
        final Order order = automaticallyInitializeOrder(orderType, OrderStatus.SERVED);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.startDelivery(orderId)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태가 '서빙됨'이 아니면 배달을 시작할 수 없다")
    @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void throwExceptionNotServedStatusWhenStartingDelivery(final OrderStatus orderStatus) {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, orderStatus);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.startDelivery(orderId)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 완료한다")
    @Test
    void completeDelivery() {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);
        setUpStubsForCompleteDeliveryOrder(order);

        // when
        final Order result = sut.completeDelivery(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 상태가 '배달중'이 아니면 배달을 완료 할 수 없다")
    @EnumSource(value = OrderStatus.class, names = {"DELIVERING"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void throwExceptionWhenCompletingDelivery(final OrderStatus orderStatus) {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, orderStatus);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.completeDelivery(orderId)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문을 완료한다")
    @Test
    void completeDeliveryOrder() {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);
        setUpStubsForCompleteOrder(order);

        // when
        final Order result = sut.complete(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
@DisplayName("배달 주문의 상태가 '배달완료'가 아니면 주문을 완료 할 수 없다")
    @EnumSource(value = OrderStatus.class, names = {"DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void throwExceptionWhenCompletingOrder(final OrderStatus orderStatus) {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.DELIVERY, orderStatus);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.complete(orderId)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("포장 주문을 완료한다")
    @Test
    void completeTakeoutOrder() {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.TAKEOUT, OrderStatus.SERVED);
        setUpStubsForCompleteOrder(order);

        // when
        final Order result = sut.complete(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장 주문의 상태가 '서빙됨'이 아니면 주문을 완료 할 수 없다")
    @EnumSource(value = OrderStatus.class, names = {"SERVED", "DELIVERING", "DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void throwExceptionWhenCompletingTakeoutOrder(final OrderStatus orderStatus) {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.TAKEOUT, orderStatus);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.complete(orderId)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 식사 주문을 완료한다")
    @Test
    void completeEatInOrder() {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.EAT_IN, OrderStatus.SERVED);
        setUpStubsForCompleteOrder(order);

        // when
        final Order result = sut.complete(order.getId());

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            softAssertions.assertThat(result.getOrderTable().isOccupied()).isFalse();
            softAssertions.assertThat(result.getOrderTable().getNumberOfGuests()).isZero();
        });
    }

    @DisplayName("매장 주문의 상태가 '서빙됨'이 아니면 주문을 완료 할 수 없다")
    @EnumSource(value = OrderStatus.class, names = {"SERVED", "DELIVERING", "DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void throwExceptionWhenCompletingEatInOrder(final OrderStatus orderStatus) {
        // given
        final Order order = automaticallyInitializeOrder(OrderType.EAT_IN, orderStatus);
        setUpStubsForStartDeliveryOrder(order);

        // expected
        final UUID orderId = order.getId();
        assertThatThrownBy(() -> sut.complete(orderId)).isInstanceOf(IllegalStateException.class);
    }

    private void setUpStubsForCreatingOrder(final Order order) {
        lenient().when(menuRepository.findAllByIdIn(any()))
                .thenReturn(List.of(order.getOrderLineItems().get(FIRST_ELEMENT).getMenu()));

        lenient().when(menuRepository.findById(any()))
                .thenReturn(Optional.of(order.getOrderLineItems().get(FIRST_ELEMENT).getMenu()));

        final OrderTable orderTable = order.getOrderTable();
        if (orderTable != null) {
            lenient().when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        }
    }

    private void setUpStubsForAcceptingOrder(final Order order) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    }

    private void setUpStubsForServingOrder(final Order order) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    }

    private void setUpStubsForStartDeliveryOrder(final Order order) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    }

    private void setUpStubsForCompleteDeliveryOrder(final Order order) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    }

    private void setUpStubsForCompleteOrder(final Order order) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    }
}