package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.makeTestOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);

    @DisplayName("매장 테이블은 등록이 가능하다")
    @Test
    void create() {
        // given
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(orderTable);

        // when
        OrderTable resultOrderTable = orderTableService.create(orderTable);

        // then
        assertThat(resultOrderTable.getId()).isNotNull();
        assertThat(resultOrderTable.getName()).isEqualTo(orderTable.getName());
        assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        assertThat(resultOrderTable.isOccupied()).isEqualTo(orderTable.isOccupied());

    }

    @DisplayName("매장 테이블명은 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nullName(String name) {
        // given
        OrderTable orderTable = makeTestOrderTable(name, 0);
        // then
        assertThatThrownBy(() -> orderTableService.create(orderTable)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 테이블은 매장 방문 고객이 착석이 가능하다.")
    @Test
    void sit() {
        // given
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(orderTable));

        // when
        OrderTable resultOrderTable = orderTableService.sit(orderTable.getId());

        // then
        assertThat(resultOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("매장 테이블은 청소가 가능하다.")
    @Test
    void clear() {
        // given
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        OrderTable resultOrderTable = orderTableService.clear(orderTable.getId());

        // then
        assertThat(resultOrderTable.isOccupied()).isFalse();
        assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("매장 테이블을 사용하는 인원은 늘어나거나 줄어들 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        OrderTable changeOrderTable = makeTestOrderTable("1번 테이블", 1);
        changeOrderTable.setOccupied(true);

        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(changeOrderTable));

        // when
        OrderTable resultOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable);

        // then
        assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(changeOrderTable.getNumberOfGuests());
    }
}