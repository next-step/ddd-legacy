package mission.step3;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @Test
    @DisplayName("주문 테이블을 생성한다")
    void create() {
        // given
        OrderTable request = new OrderTable();
        request.setName("1번 테이블");

        given(orderTableRepository.save(any(OrderTable.class)))
                .willAnswer(invocation -> {
                    OrderTable saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        // when
        OrderTable created = orderTableService.create(request);

        // then
        assertThat(created.getName()).isEqualTo("1번 테이블");
        assertThat(created.getNumberOfGuests()).isZero();
        assertThat(created.isOccupied()).isFalse();
        verify(orderTableRepository).save(any(OrderTable.class));
    }

    @Test
    @DisplayName("테이블 이름이 null이면 예외가 발생한다")
    void createWithNullName() {
        // given
        OrderTable request = new OrderTable();
        request.setName(null);

        // when & then
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블 이름이 빈 문자열이면 예외가 발생한다")
    void createWithEmptyName() {
        // given
        OrderTable request = new OrderTable();
        request.setName("");

        // when & then
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블에 손님을 착석시킨다")
    void sit() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable orderTable = new OrderTable();
        orderTable.setId(tableId);
        orderTable.setOccupied(false);

        given(orderTableRepository.findById(tableId))
                .willReturn(Optional.of(orderTable));

        // when
        OrderTable sat = orderTableService.sit(tableId);

        // then
        assertThat(sat.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블을 비운다")
    void clear() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable orderTable = new OrderTable();
        orderTable.setId(tableId);
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(4);

        given(orderTableRepository.findById(tableId))
                .willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
                .willReturn(false);

        // when
        OrderTable cleared = orderTableService.clear(tableId);

        // then
        assertThat(cleared.isOccupied()).isFalse();
        assertThat(cleared.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("완료되지 않은 주문이 있는 테이블은 비울 수 없다")
    void clearWithIncompleteOrders() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable orderTable = new OrderTable();
        orderTable.setId(tableId);

        given(orderTableRepository.findById(tableId))
                .willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> orderTableService.clear(tableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("손님 수를 변경한다")
    void changeNumberOfGuests() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable orderTable = new OrderTable();
        orderTable.setId(tableId);
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(2);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        given(orderTableRepository.findById(tableId))
                .willReturn(Optional.of(orderTable));

        // when
        OrderTable changed = orderTableService.changeNumberOfGuests(tableId, request);

        // then
        assertThat(changed.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    @DisplayName("비어있는 테이블의 손님 수는 변경할 수 없다")
    void changeNumberOfGuestsWithEmptyTable() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable orderTable = new OrderTable();
        orderTable.setId(tableId);
        orderTable.setOccupied(false);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        given(orderTableRepository.findById(tableId))
                .willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(tableId, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("손님 수를 음수로 변경할 수 없다")
    void changeNumberOfGuestsWithNegativeNumber() {
        // given
        UUID tableId = UUID.randomUUID();
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        // when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(tableId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 주문 테이블을 조회한다")
    void findAll() {
        // given
        OrderTable table1 = new OrderTable();
        table1.setId(UUID.randomUUID());
        table1.setName("1번 테이블");

        OrderTable table2 = new OrderTable();
        table2.setId(UUID.randomUUID());
        table2.setName("2번 테이블");

        given(orderTableRepository.findAll())
                .willReturn(List.of(table1, table2));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables).hasSize(2);
        assertThat(orderTables.get(0).getName()).isEqualTo("1번 테이블");
        assertThat(orderTables.get(1).getName()).isEqualTo("2번 테이블");
    }
}
