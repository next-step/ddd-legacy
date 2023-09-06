package kitchenpos.application;

import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_TABLE_NUMBER_OF_GUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.fakerepository.MenuFakeRepository;
import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
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
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("OrderService 클래스의 complete메소드 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceCompleteTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient mockRidersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderFakeRepository();
        menuRepository = new MenuFakeRepository();
        orderTableRepository = new OrderTableFakeRepository();

        sut = new OrderService(orderRepository, menuRepository,
            orderTableRepository, mockRidersClient);
    }

    @Test
    void 주문이_없으면_예외를_발생시킨다() {

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }


    @Test
    void 배달주문일_때_주문의_상태가_delivered가_아니면_예외를_발생시킨다() {
        // given
        final Order deliveringOrder = orderRepository.save(
            createDelieveryOrder(OrderStatus.DELIVERING));
        final Order completedOrder = orderRepository.save(
            createDelieveryOrder(OrderStatus.COMPLETED));

        // when & then
        assertThatThrownBy(() -> sut.complete(deliveringOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> sut.complete(completedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }


    @Test
    void 매장주문일_때_served가_아니면_예외를_발생시킨다() {
        // given
        final Order acceptedOrder = orderRepository.save(createEatinOrder(OrderStatus.ACCEPTED));
        final Order completedOrder = orderRepository.save(createEatinOrder(OrderStatus.COMPLETED));

        // when & then
        assertThatThrownBy(() -> sut.complete(acceptedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> sut.complete(completedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }


    @Test
    void 포장주문일_때_served가_아니면_예외를_발생시킨다() {
        // given
        final Order acceptedOrder = orderRepository.save(createTakeoutOrder(OrderStatus.ACCEPTED));
        final Order completedOrder
            = orderRepository.save(createTakeoutOrder(OrderStatus.COMPLETED));

        // when & then
        assertThatThrownBy(() -> sut.complete(acceptedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> sut.complete(completedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 포장주문일_때_주문의_상태를_completed로_변경하여_반환한다() {
        // given
        final Order order = orderRepository.save(createTakeoutOrder(OrderStatus.SERVED));

        // when
        final Order actual = sut.complete(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    @Test
    void 배달주문일_때_주문의_상태를_completed로_변경하여_반환한다() {
        // given
        final Order order = orderRepository.save(createDelieveryOrder(OrderStatus.DELIVERED));

        // when
        final Order actual = sut.complete(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    @Test
    void 매장주문일_때_주문의_상태를_completed로_변경하여_반환한다() {
        // given
        final OrderTable orderTable = orderTableRepository.save(createOccupiedOrderTable());
        final Order order = orderRepository.save(createEatinOrderWithOrderTable(orderTable));

        // when
        final Order actual = sut.complete(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 매장주문일_때_주문에_할당된_매장테이블을_치운다() {
        // given
        final OrderTable orderTable = orderTableRepository.save(createOccupiedOrderTable());
        final Order order = orderRepository.save(createEatinOrderWithOrderTable(orderTable));

        // when
        sut.complete(order.getId());

        // then
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }


    final OrderTable createOccupiedOrderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(TEST_ORDER_TABLE_NUMBER_OF_GUEST);
        orderTable.setOccupied(true);

        return orderTable;
    }

    final Order createEatinOrderWithOrderTable(final OrderTable orderTable) {
        final Order order = new Order();

        order.setStatus(OrderStatus.SERVED);
        order.setType(OrderType.EAT_IN);
        order.setOrderTable(orderTable);

        return order;
    }

    final Order createDelieveryOrder(final OrderStatus status) {
        return create(OrderType.DELIVERY, status);
    }

    final Order createEatinOrder(final OrderStatus status) {
        return create(OrderType.EAT_IN, status);
    }

    final Order createTakeoutOrder(final OrderStatus status) {
        return create(OrderType.TAKEOUT, status);
    }

    final Order create(final OrderType type, final OrderStatus status) {
        final Order order = new Order();
        order.setType(type);
        order.setStatus(status);

        return order;
    }
}
