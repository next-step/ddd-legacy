package kitchenpos.unit;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderTableTest extends UnitTestRunner {

    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @DisplayName("주문 테이블을 생성 한다.")
    @Test
    public void create() {
        //given
        final OrderTable request = new OrderTable();
        String orderTableName = "1번 테이블";
        request.setName(orderTableName);

        final OrderTable stubbedOrderTable = new OrderTable();
        stubbedOrderTable.setName(orderTableName);
        stubbedOrderTable.setId(UUID.randomUUID());
        stubbedOrderTable.setNumberOfGuests(0);
        stubbedOrderTable.setEmpty(true);

        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(stubbedOrderTable);

        //when
        final OrderTable savedOrderTable = orderTableService.create(request);

        //then
        assertAll(
                () -> assertThat(savedOrderTable.getName()).isEqualTo(orderTableName),
                () -> assertThat(savedOrderTable.getId()).isNotNull(),
                () -> assertThat(savedOrderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(savedOrderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 테이블 착석")
    @Test
    public void sit() {
        //given
        final UUID orderTableId = UUID.randomUUID();
        String orderTableName = "1번 테이블";

        final OrderTable stubbedOrderTable = new OrderTable();
        stubbedOrderTable.setName(orderTableName);
        stubbedOrderTable.setId(orderTableId);
        stubbedOrderTable.setNumberOfGuests(0);
        stubbedOrderTable.setEmpty(true);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(stubbedOrderTable));

        //when
        final OrderTable sitTable = orderTableService.sit(orderTableId);

        //then
        assertThat(sitTable.isEmpty()).isFalse();
    }

    @DisplayName("주문 테이블 Clear")
    @Test
    public void clear() {
        //given
        final UUID orderTableId = UUID.randomUUID();
        String orderTableName = "1번 테이블";

        final OrderTable stubbedOrderTable = new OrderTable();
        stubbedOrderTable.setName(orderTableName);
        stubbedOrderTable.setId(orderTableId);
        stubbedOrderTable.setNumberOfGuests(3);
        stubbedOrderTable.setEmpty(false);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(stubbedOrderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(stubbedOrderTable, OrderStatus.COMPLETED)).thenReturn(false);

        //when
        final OrderTable orderTable = orderTableService.clear(orderTableId);

        //then
        assertAll(
                () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(orderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 테이블 인원수 변경")
    @Test
    public void changeNumberOfGuests() {
        //given
        final UUID orderTableId = UUID.randomUUID();
        String orderTableName = "1번 테이블";

        final OrderTable stubbedOrderTable = new OrderTable();
        stubbedOrderTable.setName(orderTableName);
        stubbedOrderTable.setId(orderTableId);
        stubbedOrderTable.setNumberOfGuests(3);
        stubbedOrderTable.setEmpty(false);

        final OrderTable request = new OrderTable();
        final int changeNumber = 5;
        request.setNumberOfGuests(changeNumber);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(stubbedOrderTable));

        //when
        final OrderTable changeNumberOfGuestsTable = orderTableService.changeNumberOfGuests(orderTableId, request);

        //then
        assertThat(changeNumberOfGuestsTable.getNumberOfGuests()).isEqualTo(changeNumber);
    }

    @DisplayName("모든 주문 테이블 조회")
    @Test
    public void findAll() {
        //given
        final OrderTable stubbedOrderTable_1 = new OrderTable();
        stubbedOrderTable_1.setName("1번 테이블");
        stubbedOrderTable_1.setId(UUID.randomUUID());
        stubbedOrderTable_1.setNumberOfGuests(3);
        stubbedOrderTable_1.setEmpty(false);

        final OrderTable stubbedOrderTable_2 = new OrderTable();
        stubbedOrderTable_2.setName("2번 테이블");
        stubbedOrderTable_2.setId(UUID.randomUUID());
        stubbedOrderTable_2.setNumberOfGuests(6);
        stubbedOrderTable_2.setEmpty(false);

        final OrderTable stubbedOrderTable_3 = new OrderTable();
        stubbedOrderTable_3.setName("3번 테이블");
        stubbedOrderTable_3.setId(UUID.randomUUID());
        stubbedOrderTable_3.setNumberOfGuests(0);
        stubbedOrderTable_3.setEmpty(true);

        when(orderTableRepository.findAll()).thenReturn(List.of(stubbedOrderTable_1, stubbedOrderTable_2, stubbedOrderTable_3));

        //when
        final List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertAll(
                () -> assertThat(orderTables.size()).isEqualTo(3),
                () -> assertThat(orderTables.get(0)).isEqualTo(stubbedOrderTable_1),
                () -> assertThat(orderTables.get(1)).isEqualTo(stubbedOrderTable_2),
                () -> assertThat(orderTables.get(2)).isEqualTo(stubbedOrderTable_3)
        );
    }

}
