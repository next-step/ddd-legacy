package kitchenpos.application;

import kitchenpos.domain.*;
import factory.OrderTableFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 생성할 수 있다.")
    @Test
    void create() {
        final OrderTable request = OrderTableFactory.of();

        final OrderTable orderTable = orderTableService.create(request);

        assertThat(orderTable.getId()).isNotNull();
    }

    @DisplayName("주문 테이블 생성 시, 이름은 필수로 입력되어야 한다.")
    @Test
    void create_input_name() {
        final OrderTable request = new OrderTable();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("손님은 테이블에 앉을 수 있다.")
    @Test
    void sit() {
        final OrderTable orderTable = OrderTableFactory.of();
        final OrderTable request = orderTableRepository.save(orderTable);

        final OrderTable actual = orderTableService.sit(request.getId());

        assertThat(actual.isOccupied()).isTrue();
    }

    @DisplayName("테이블을 청소한다. (정보를 초기화 한다)")
    @Test
    void clear() {
        final OrderTable orderTable = OrderTableFactory.of();
        final OrderTable request = orderTableRepository.save(orderTable);

        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderTable(orderTable);
        orderRepository.save(order);

        final OrderTable actual = orderTableService.clear(request.getId());

        assertThat(actual.isOccupied()).isFalse();
        assertThat(actual.getNumberOfGuests()).isZero();
    }

    @DisplayName("테이블을 청소 시, 완료되지 않는 주문이 없어야 한다.")
    @Test
    void clear_order_status_is_completed() {
        final OrderTable orderTable = OrderTableFactory.of();
        final OrderTable request = orderTableRepository.save(orderTable);

        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(orderTable);
        orderRepository.save(order);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(request.getId()));
    }

    @DisplayName("주문 테이블의 손님 숫자를 수정한다.")
    @Test
    void changeNumberOfGuests() {
        final OrderTable orderTable = OrderTableFactory.of(true);

        final OrderTable request = orderTableRepository.save(orderTable);
        request.setNumberOfGuests(2);

        OrderTable actual = orderTableService.changeNumberOfGuests(request.getId(), request);
        assertThat(actual.getNumberOfGuests()).isEqualTo(2);
    }


    @DisplayName("손님 수 변경 시, 음수 일수 없다.")
    @Test
    void changeNumberOfGuests_negative() {
        final OrderTable orderTable = OrderTableFactory.of();

        final OrderTable request = orderTableRepository.save(orderTable);
        request.setNumberOfGuests(-1);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request));
    }

    @DisplayName("손님 수 변경 시, 사용중인 테이블이 아닐경우 변경할 수 없다")
    @Test
    void changeNumberOfGuests_occupied() {
        final OrderTable orderTable = OrderTableFactory.of(false);

        final OrderTable request = orderTableRepository.save(orderTable);
        request.setNumberOfGuests(2);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request));
    }

    @DisplayName("주문 테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        final OrderTable orderTable = OrderTableFactory.of();
        orderTableRepository.save(orderTable);

        List<OrderTable> actual = orderTableService.findAll();

        assertThat(actual).hasSize(1);
    }
}
