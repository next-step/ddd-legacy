package kitchenpos.application;

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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    private static final UUID DEFAULT_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "1번 테이블";
    private static final int DEFAULT_GUEST = 0;

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    static OrderTable defaultOrderTable() {
        return createOrderTable(DEFAULT_ID, DEFAULT_NAME, DEFAULT_GUEST);
    }

    private static OrderTable createOrderTable(final UUID ID, final String name, final int guest) {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(ID);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(guest);

        return orderTable;
    }

    @DisplayName("주문 테이블 생성이 가능하다")
    @Test
    void create_order_table() {
        final OrderTable orderTable = defaultOrderTable();

        given(orderTableRepository.save(Mockito.any(OrderTable.class)))
                .willReturn(orderTable);

        final OrderTable result = orderTableService.create(orderTable);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEFAULT_ID);
        assertThat(result.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 이름은 필수이다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_order_table_with_nll_and_empty_name(final String name) {
        final OrderTable orderTable = createOrderTable(DEFAULT_ID, name, DEFAULT_GUEST);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블의 사람이 앉을 수 있다")
    @Test
    void sit() {
        final OrderTable orderTable = defaultOrderTable();

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final OrderTable result = orderTableService.sit(DEFAULT_ID);
        assertThat(result).isNotNull();
        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("주문이 완료된 상태라면 주문 테이블을 치울 수 있다")
    @Test
    void clear() {
        final OrderTable orderTable = createOrderTable(DEFAULT_ID, DEFAULT_NAME, 5);

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        final OrderTable result = orderTableService.clear(DEFAULT_ID);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블에 손님이 앉았다면 앉아 있는 손님의 숫자를 변경 할 수 있다")
    @Test
    void change_number_of_guest() {
        final OrderTable defaultOrderTable = defaultOrderTable();
        defaultOrderTable.setOccupied(true);

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultOrderTable));

        final int changeGuest = 3;
        final OrderTable changeOrderTable = createOrderTable(DEFAULT_ID, DEFAULT_NAME, changeGuest);

        final OrderTable result = orderTableService.changeNumberOfGuests(DEFAULT_ID, changeOrderTable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(changeGuest);
    }

    @DisplayName("주문 테이블을 사용 가능한 손님의 숫자는 음수 일 수 없다")
    @Test
    void change_number_of_guest_by_negative_number() {
        final OrderTable defaultOrderTable = defaultOrderTable();
        defaultOrderTable.setOccupied(true);

        final int changeGuest = -1;
        final OrderTable changeOrderTable = createOrderTable(DEFAULT_ID, DEFAULT_NAME, changeGuest);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(DEFAULT_ID, changeOrderTable));
    }

    @DisplayName("사람이 앉아 있지 않는 상태의 주문 테이블은 손님의 숫자를 변경할 수 없다")
    @Test
    void change_number_of_guest_in_occupied() {
        final OrderTable defaultOrderTable = defaultOrderTable();

        final int changeGuest = 3;
        final OrderTable changeOrderTable = createOrderTable(DEFAULT_ID, DEFAULT_NAME, changeGuest);

        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultOrderTable));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(DEFAULT_ID, changeOrderTable));
    }

    @DisplayName("생성된 주문 테이블을 조회 할 수 있다")
    @Test
    void select_all_order_tables() {
        final OrderTable defaultOrderTable = defaultOrderTable();
        final OrderTable secondOrderTable = createOrderTable(UUID.randomUUID(), "2번 테이블", DEFAULT_GUEST);

        final List<OrderTable> orderTables = Arrays.asList(defaultOrderTable, secondOrderTable);

        given(orderTableRepository.findAll())
                .willReturn(orderTables);

        final List<OrderTable> result = orderTableService.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(orderTables);
    }
}