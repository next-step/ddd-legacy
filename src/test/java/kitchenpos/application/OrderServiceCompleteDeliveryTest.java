package kitchenpos.application;

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
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceCompleteDeliveryTest {

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

    @DisplayName("주문이 없으면 예외를 발생시킨다")
    @Test
    void completeDelivery_주문이_없으면_예외를_발생시킨다() {

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("주문의 상태가 delivering이 아니면 예외를 발생시킨다")
    @Test
    void completeDelivery_주문의_상태가_delivering이_아니면_예외를_발생시킨다() {
        // given
        final Order servedOrder = orderRepository.save(create(OrderStatus.SERVED));
        final Order deliveredOrder = orderRepository.save(create(OrderStatus.DELIVERED));

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(servedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> sut.completeDelivery(deliveredOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }


    @DisplayName("주문의 상태를 delivered로 변경하여 반환한다")
    @Test
    void completeDelivery_주문의_상태를_delivered로_변경하여_반환한다() {
        // given
        final Order order = orderRepository.save(createDeliveringDeliveryOrder());

        // when
        final Order actual = sut.completeDelivery(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }


    final Order createDeliveringDeliveryOrder() {
        return create(OrderStatus.DELIVERING);
    }

    final Order create(final OrderStatus orderStatus) {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);

        return order;
    }
}
