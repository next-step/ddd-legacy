package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.InMemoryOrderRepository;
import kitchenpos.infra.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("테이블 이름이 null이거나 빈 문자열일 경우 IllegalArgumentException이 발생한다.")
    void create_fail_for_null_or_empty_name(String name) {
        OrderTable request = OrderTableRequestBuilder.builder()
            .withName(name)
            .build();

        assertThatThrownBy(() -> orderTableService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블을 생성한다.")
    public void create_success() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable response = orderTableService.create(request);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("테이블이 존재하지 않는데 테이블 점유 시도할 시 NoSuchElementException이 발생한다.")
    void sit_fail_for_not_existing_table() {
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("테이블을 점유한다.")
    void sit_success() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable orderTable = orderTableService.create(request);

        OrderTable response = orderTableService.sit(orderTable.getId());
        assertThat(response.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블이 존재하지 않는데 테이블 비우기 시도할 시 NoSuchElementException이 발생한다.")
    void clear_fail_for_not_existing_table() {
        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 상태가 완료가 아닌 테이블을 비우기 시도할 시 IllegalStateException이 발생한다.")
    void clear_fail_for_order_status_is_not_completed() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable orderTable = orderTableService.create(request);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블을 비운다.")
    void clear_success() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable orderTable = orderTableService.create(request);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        OrderTable response = orderTableService.clear(orderTable.getId());
        assertThat(response.getNumberOfGuests()).isEqualTo(0);
        assertThat(response.isOccupied()).isFalse();
    }
}

