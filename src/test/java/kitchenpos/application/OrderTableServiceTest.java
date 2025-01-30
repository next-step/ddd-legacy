package kitchenpos.application;

import static kitchenpos.builder.TestFixtureFactory.createEmptyOrderTable;
import static kitchenpos.builder.TestFixtureFactory.createUsingOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderTableServiceTest {

    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableRepository = mock(OrderTableRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    @DisplayName("매장 테이블을 만들 수 있다")
    void create() {
        // given
        OrderTable request = createOrderTableRequest();
        when(orderTableRepository.save(any(OrderTable.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderTable created = orderTableService.create(request);

        // then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("1번 테이블");
        assertThat(created.getNumberOfGuests()).isZero();
        assertThat(created.isOccupied()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("매장 테이블 이름이 없으면 예외가 발생한다.")
    void orderTable_name_exception(String name) {
        // given
        OrderTable request = new OrderTable(name, 0, false);

        // when // then
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 매장 테이블을 볼 수 있다")
    void find_all() {
        // given
        List<OrderTable> orderTables = List.of(
                createEmptyOrderTable(),
                createUsingOrderTable()
        );
        when(orderTableRepository.findAll()).thenReturn(orderTables);

        // when
        List<OrderTable> found = orderTableService.findAll();

        // then
        assertThat(found).hasSize(2);
    }

    @Test
    @DisplayName("빈 테이블을 이용할 수 있다")
    void sit() {
        // given
        OrderTable orderTable = createEmptyOrderTable();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when
        OrderTable occupied = orderTableService.sit(orderTable.getId());

        // then
        assertThat(occupied.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("모든 주문이 완료되면 테이블을 정리할 수 있다")
    void clear() {
        // given
        OrderTable orderTable = createUsingOrderTable();

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        // when
        OrderTable cleared = orderTableService.clear(orderTable.getId());

        // then
        assertThat(cleared.isOccupied()).isFalse();
        assertThat(cleared.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("주문이 완료되지 않은 테이블을 정리할 시 예외가 발생한다.")
    void orderTable_occupied_exception() {
        // given
        OrderTable orderTable = createUsingOrderTable();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED))).thenReturn(true);

        // when // then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블에 손님이 있는 경우에만 앉아 있는 손님의 수를 변경할 수 있다")
    void change_numberOfGuests() {
        // given
        OrderTable orderTable = createUsingOrderTable();
        orderTable.setNumberOfGuests(4);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when
        OrderTable changed = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(changed.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    @DisplayName("빈 테이블의 손님 수 변경 시 예외가 발생한다.")
    void change_numberOfGuests_not_occupied_exception() {
        // given
        OrderTable orderTable = createEmptyOrderTable();
        orderTable.setNumberOfGuests(4);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("손님 수를 음수로 변경하면 예외가 발생한다..")
    void change_numberOfGuests_negative_number_exception() {
        // given
        OrderTable orderTable = createUsingOrderTable();
        orderTable.setNumberOfGuests(-1);

        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private OrderTable createOrderTableRequest() {
        return new OrderTable("1번 테이블", 0, false);
    }
}
