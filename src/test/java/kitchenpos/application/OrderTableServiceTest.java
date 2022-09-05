package kitchenpos.application;

import kitchenpos.application.support.TestFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {


    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("주문 테이블 생성이 가능하다")
    @Test
    void create_order_table() {
        final OrderTable orderTable = TestFixture.createFirstOrderTable();

        given(orderTableRepository.save(Mockito.any(OrderTable.class)))
                .willReturn(orderTable);

        final OrderTable result = orderTableService.create(orderTable);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestFixture.FIRST_ORDER_TABLE_ID);
        assertThat(result.getName()).isEqualTo(TestFixture.FIRST_ORDER_TABLE_NAME);
        assertThat(result.getNumberOfGuests()).isEqualTo(TestFixture.FIRST_ORDER_TABLE_GUEST);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 이름이 Null이거나 비어있다면 IllegalArgumentException를 발생시킨다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_order_table_with_nll_and_empty_name(final String name) {
        OrderTable orderTable = TestFixture.createFirstOrderTable();
        orderTable.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블의 사람이 앉을 수 있다")
    @Test
    void sit() {
        final OrderTable orderTable = TestFixture.createFirstOrderTable();

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final OrderTable result = orderTableService.sit(TestFixture.FIRST_ORDER_TABLE_ID);
        assertThat(result).isNotNull();
        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("주문이 완료된 상태라면 주문 테이블을 치울 수 있다")
    @Test
    void clear() {
        OrderTable orderTable = TestFixture.createFirstOrderTable();
        orderTable.setNumberOfGuests(5);

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final OrderTable result = orderTableService.clear(TestFixture.FIRST_ORDER_TABLE_ID);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블에 손님이 앉았다면 앉아 있는 손님의 숫자를 변경 할 수 있다")
    @Test
    void change_number_of_guest() {
        OrderTable orderTable = TestFixture.createFirstOrderTable();
        orderTable.setOccupied(true);

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final int changeGuest = 3;
        orderTable.setNumberOfGuests(changeGuest);

        final OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(changeGuest);
    }

    @DisplayName("주문 테이블을 사용 가능한 손님의 숫자는 음수라면 IllegalArgumentException를 발생시킨다")
    @Test
    void change_number_of_guest_by_negative_number() {
        OrderTable orderTable = TestFixture.createFirstOrderTable();
        orderTable.setOccupied(true);

        final int changeGuest = -1;
        orderTable.setNumberOfGuests(changeGuest);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("비어있는 테이블의 손님 숫자를 변경하면 IllegalStateException를 발생시킨다")
    @Test
    void change_number_of_guest_in_occupied() {
        OrderTable orderTable = TestFixture.createFirstOrderTable();

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final int changeGuest = 3;
        orderTable.setNumberOfGuests(changeGuest);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("생성된 주문 테이블을 조회 할 수 있다")
    @Test
    void select_all_order_tables() {
        final OrderTable firstOrderTable = TestFixture.createFirstOrderTable();
        final OrderTable secondOrderTable = TestFixture.createSecondOrderTable();

        final List<OrderTable> orderTables = Arrays.asList(firstOrderTable, secondOrderTable);

        given(orderTableRepository.findAll())
                .willReturn(orderTables);

        final List<OrderTable> result = orderTableService.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(orderTables);
    }
}
