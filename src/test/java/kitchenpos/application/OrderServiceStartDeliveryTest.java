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
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("OrderService의 startDelivery메소드 테스트")
public class OrderServiceStartDeliveryTest {

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
        assertThatThrownBy(() -> sut.startDelivery(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 배달주문이_아니면_예외를_발생시킨다() {
        // given
        final Order eatinOrder = orderRepository.save(create(OrderType.EAT_IN));
        final Order takeoutOrder = orderRepository.save(create(OrderType.TAKEOUT));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(eatinOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> sut.startDelivery(takeoutOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문의_상태가_served가_아니면_예외를_발생시킨다() {
        // given
        final Order order = orderRepository.save(create(OrderStatus.ACCEPTED));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(order.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문의_상태를_delivering으로_변경하여_반환한다() {
        // given
        final Order order = orderRepository.save(createServedDeliveryOrder());

        // when
        final Order actual = sut.startDelivery(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }


    final Order createServedDeliveryOrder() {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        return order;
    }

    final Order create(final OrderType orderType) {
        final Order order = new Order();
        order.setType(orderType);

        return order;
    }

    final Order create(final OrderStatus orderStatus) {
        final Order order = new Order();
        order.setStatus(orderStatus);

        return order;
    }
}
