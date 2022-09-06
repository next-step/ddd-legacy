package kitchenpos.unit;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderTableService orderTableService;

    @ParameterizedTest(name = "주문 테이블의 이름은 빈 값일 수 없다. source = {0}")
    @NullAndEmptySource
    void create_Illegal_EmptyName(String source) {
        // given
        OrderTable request = anOrderTable(false);
        request.setName(source);

        /// when + then
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블 생성")
    @Test
    void create() {
        // given
        OrderTable request = anOrderTable(false);
        request.setName("1번 테이블");

        when(orderTableRepository.save(any())).then(i -> i.getArgument(0, OrderTable.class));

        // when
        OrderTable orderTable = orderTableService.create(request);

        // then
        assertAll(
                () -> assertThat(orderTable.getId()).isNotNull(),
                () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문 테이블을 비점유 상태로 만드려면 해당 테이블에서 진행중인 주문이 없어야 한다.")
    @Test
    void clear_Illegal_OrderNotComplete() {
        // given
        OrderTable orderTable = anOrderTable(true);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블을 비점유 상태로 변경")
    @Test
    void clear() {
        // given
        OrderTable orderTable = anOrderTable(true);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        OrderTable clearedTable = orderTableService.clear(orderTable.getId());

        // then
        assertAll(
                () -> assertThat(clearedTable.isOccupied()).isFalse(),
                () -> assertThat(clearedTable.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("주문 테이블 손님 수를 변경할 때 0명보다 작을 수 없다.")
    @Test
    void changeNumberOfGuests_Illegal_NegativeNumberOfGuests() {
        // given
        OrderTable orderTable = anOrderTable("1번 테이블", true, 3);

        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(-1);

        // when + then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블 손님 수를 변경할 때 테이블은 점유 상태여야한다.")
    @Test
    void changeNumberOfGuests_Illegal_TableNotOccupied() {
        // given
        OrderTable orderTable = anOrderTable("1번 테이블", false, 3);

        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(4);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        // when + then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블 손님 수 변경")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable orderTable = anOrderTable("1번 테이블", true, 3);

        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(4);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        // when
        OrderTable changedTable = orderTableService.changeNumberOfGuests(orderTable.getId(), changeRequest);

        // then
        assertThat(changedTable.getNumberOfGuests()).isEqualTo(4);
    }
}
