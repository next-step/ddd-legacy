package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final OrderTableService orderTableService = new OrderTableService(orderTableRepository,
        orderRepository);

    @Test
    @DisplayName("주문 테이블 생성 시 이름이 없을 경우 예외를 발생 시킨다.")
    void create() {
        var request = new OrderTable();
        assertThatThrownBy(() -> orderTableService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 테이블 이름이 필요 합니다.")
        ;
    }

    @Test
    @DisplayName("주문 테이블 앉기 시 주문 테이블은 점유 상태로 변경 된다.")
    void sit() {
        // given
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.FALSE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when
        var response = orderTableService.sit(UUID.randomUUID());

        // then
        assertThat(response.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("주문 테이블 청소 시 주문 완료 상태가 아닌 경우 예외를 발생 시킨다.")
    void clearWithNoComplete() {
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.FALSE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
        ;
    }

    @Test
    @DisplayName("주문 테이블을 청소 할 수 있다.")
    void clear() {
        // given
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.TRUE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(
            Boolean.FALSE);

        // when
        var response = orderTableService.clear(UUID.randomUUID());

        // then
        assertThat(response.isOccupied()).isFalse();
        assertThat(response.getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    @DisplayName("주문 테이블 손님 수 변경이 0보다 작은 경우 예외를 발생 시킨다.")
    void changeNumberOfGuestsLessThanZero() {
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.FALSE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.TRUE);

        var request = new OrderTable();
        request.setNumberOfGuests(-1);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("0보다 작은 수로 변경 할 수 없습니다.")
        ;
    }

    @Test
    @DisplayName("주문 테이블 손님 수 변경 시 점유 상태가 아닌 경우 예외를 발생 시킨다.")
    void changeNumberOfGuestsNoOccupied() {
        // given
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.FALSE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.TRUE);

        // when
        var request = new OrderTable();
        request.setNumberOfGuests(10);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("점유 상태가 아닌 경우 변경 할 수 없습니다.")
        ;
    }

    @Test
    @DisplayName("주문 테이블 손님 수를 변경 할 수 있다.")
    void changeNumberOfGuests() {
        // given
        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.TRUE);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.TRUE);

        // when
        var request = new OrderTable();
        request.setNumberOfGuests(10);
        var response = orderTableService.changeNumberOfGuests(UUID.randomUUID(), request);

        // then
        assertThat(response.getNumberOfGuests()).isEqualTo(10);
    }
}