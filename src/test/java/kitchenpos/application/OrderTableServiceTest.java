package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderTableService orderTableService;

    @ParameterizedTest
    @DisplayName("테이블의 이름은 null 이거나 빈 문자열일 수 없다")
    @NullAndEmptySource
    void createNullAndEmptyName(String name) {
        OrderTable request = new OrderTable();
        request.setName(name);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.create(request));
    }

    @Test
    @DisplayName("테이블의 이름은 null 이거나 빈 문자열일 수 없다")
    void create() {
        OrderTable request = new OrderTable();
        request.setName("테이블");

        when(orderTableRepository.save(any(OrderTable.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        OrderTable orderTable = orderTableService.create(request);

        assertThat(orderTable.getId()).isNotNull();
        assertThat(orderTable.getName()).isEqualTo("테이블");
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @Test
    @DisplayName("앉을 수 있다")
    void sit() {
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable()));

        OrderTable orderTable = orderTableService.sit(UUID.randomUUID());

        assertThat(orderTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("완료되지 않은 주문이 있다면 치울 수 없다")
    void cannotClear() {
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable()));
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class))).thenReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));

    }

    @Test
    @DisplayName("모든 주문이 완료되면 치울 수 있다")
    void clear() {
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(new OrderTable()));
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class))).thenReturn(false);

        OrderTable cleared = orderTableService.clear(UUID.randomUUID());

        assertThat(cleared.getNumberOfGuests()).isZero();
        assertThat(cleared.isOccupied()).isFalse();
    }

    @Test
    @DisplayName("손님수는 0 미만으로는 수정할 수 없다")
    void cannotChangeNegative() {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("빈 테이블은 수정할 수 없다")
    void cannotChangeNotOccupied() {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(2);

        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(false);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("손님수를 수정할 수 있다")
    void changeNumberOfGuests() {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(2);

        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(true);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        OrderTable changed = orderTableService.changeNumberOfGuests(UUID.randomUUID(), request);
        assertThat(changed.getNumberOfGuests()).isEqualTo(2);
    }


    @Test
    void findAll() {
        when(orderTableRepository.findAll()).thenReturn(List.of(new OrderTable()));

        assertThat(orderTableService.findAll()).hasSize(1);
    }
}
