package kitchenpos.application;

import static kitchenpos.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @Test
    @DisplayName("고객이 앉는 테이블 하나를 생성한다.")
    void createOrderTable() {
        // given
        OrderTable expected = ORDER_TABLE();
        given(orderTableRepository.save(any(OrderTable.class))).willReturn(expected);

        // when
        OrderTable orderTable = orderTableService.create(expected);

        // then
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("매장 테이블 이름이 있어야 한다.")
    void checkTableName(String value) {
        // given
        OrderTable expected = ORDER_TABLE();

        // when
        expected.setName(value);

        // then
        assertThatThrownBy(() -> orderTableService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블 하나에 고객이 앉으며, 해당 테이블은 비어있어야 한다.")
    void customerSitAtEmptyTable() {
        // given
        OrderTable expected = ORDER_TABLE();
        UUID orderTableId = expected.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(expected));

        // when
        assertThat(expected.isOccupied()).isFalse();
        OrderTable actual = orderTableService.sit(orderTableId);

        // then
        assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블을 치우고 다른 손님을 받을 수 있게 한다.")
    void clearTableForNextCustomer() {
        // given
        OrderTable expected = ORDER_TABLE();
        UUID orderTableId = expected.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(expected));
        given(orderRepository.existsByOrderTableAndStatusNot(expected, OrderStatus.COMPLETED))
                .willReturn(false);

        // when
        OrderTable actual = orderTableService.clear(orderTableId);

        // then
        verify(orderRepository, times(1))
                .existsByOrderTableAndStatusNot(expected, OrderStatus.COMPLETED);
        assertThat(actual.isOccupied()).isFalse();
        assertThat(actual.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("해당 테이블에 추가 주문이 없어야 한다.")
    void clearTableHasNoExtraOrder() {
        // given
        OrderTable orderTable = ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("수정되는 고객 숫자가 0 이상이어야 한다.")
    void customersGreaterThanOrEqualToZero() {
        // given
        OrderTable expected = ORDER_TABLE();
        UUID orderTableId = expected.getId();

        // when
        expected.setNumberOfGuests(-1);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("수정되는 테이블은 사람들이 이미 앉아있는 테이블이어야 한다.")
    void changeNumberOfCustomer() {
        // given
        OrderTable expected = ORDER_TABLE();
        UUID orderTableId = expected.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(expected));

        // when
        expected.setOccupied(true);
        expected.setNumberOfGuests(7);
        OrderTable actual = orderTableService.changeNumberOfGuests(orderTableId, expected);

        // then
        assertThat(actual.isOccupied()).isTrue();
        assertThat(actual.getNumberOfGuests()).isEqualTo(7);
    }

    @Test
    @DisplayName("주문 테이블 아이디를 넣어 호출 했을 때 값이 있어야 한다.")
    void orderTableExists() {
        // given
        OrderTable expected = ORDER_TABLE();
        UUID orderTableId = expected.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(new OrderTable()));

        // when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, expected))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("사용가능한_테이블만_인원_수를_변경_할_수_있다")
    void changeGuestNumberShouldOccupiedTrue() {
        // given
        OrderTable orderTable = ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // when
        orderTable.setNumberOfGuests(13);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("모든 테이블을 가져온다.")
    void findAllOrderTable() {
        // given
        given(orderTableRepository.findAll()).willReturn(List.of(new OrderTable(), new OrderTable()));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        verify(orderTableRepository, times(1)).findAll();
        assertThat(orderTables).hasSize(2);
    }
}
