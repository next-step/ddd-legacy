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
@DisplayName("OrderTableService의 serve메소드 테스트")
public class OrderServiceServeTest {

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
        assertThatThrownBy(() -> sut.serve(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 주문의_상태가_accepted가_아니면_예외를_발생시킨다() {
        // given
        final Order waitingOrder = orderRepository.save(create(OrderStatus.WAITING));
        final Order servedOrder = orderRepository.save(create(OrderStatus.SERVED));

        // when & then
        assertThatThrownBy(() -> sut.serve(waitingOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> sut.serve(servedOrder.getId()))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문의_상태를_served로_변경_후_반환한다() {
        // given
        final Order order = orderRepository.save(create(OrderStatus.ACCEPTED));

        // when
        final Order actual = sut.serve(order.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    final Order create(final OrderStatus status) {
        final Order order = new Order();
        order.setStatus(status);

        return order;
    }
}
