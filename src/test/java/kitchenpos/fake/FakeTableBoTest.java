package kitchenpos.fake;

import kitchenpos.bo.TableBo;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeTableBoTest {

    private TableBo tableBo;

    private OrderDao orderDao = new FakeOrderDao();

    private OrderTableDao orderTableDao = new FakeOrderTableDao();

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @DisplayName("테이블 리스트를 조회")
    @Test
    void list() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setTableGroupId(1L)
                .setEmpty(false)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setTableGroupId(2L)
                .setEmpty(false)
                .setId(2L)
                .setNumberOfGuests(0)
                .build()
                ;
        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        //when(orderTableDao.findAll()).thenReturn(orderTables);

        List<OrderTable> findOrderTables = tableBo.list();

        assertThat(findOrderTables).containsAll(orderTables);
        assertThat(findOrderTables.size()).isEqualTo(orderTables.size());
    }

    @DisplayName("테이블 생성")
    @Test
    void create() {
        OrderTable orderTable = new OrderTableBuilder()
                .setTableGroupId(1L)
                .setEmpty(false)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        //when(orderTableDao.save(orderTable)).thenReturn(orderTable);

        OrderTable savedOrderTable = tableBo.create(orderTable);

        assertThat(savedOrderTable.getId()).isEqualTo(orderTable.getId());
        assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("테이블 착석 상태를 비움으로 변경")
    @Test
    void changeEmpty() {
        OrderTable requestOrderTable = new OrderTableBuilder()
                .setId(1L)
                .setEmpty(true)
                .build()
                ;

        OrderTable orderTable = new OrderTableBuilder()
                .setEmpty(false)
                .setId(1L)
                .build()
                ;

        orderTableDao.save(orderTable);
        //

        //when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        //when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(),
          //      Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(false);
        //when(orderTableDao.save(orderTable)).thenReturn(orderTable);

        OrderTable changedOrderTable = tableBo.changeEmpty(orderTable.getId(), requestOrderTable);

        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블 그룹에 포함된 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByInTableGroup() {
        OrderTable orderTable = new OrderTableBuilder()
                .setId(1L)
                .setTableGroupId(1L)
                .setEmpty(false)
                .build()
                ;

        //when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("주문상태가 식사중 또는 요리중인 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByOrderStatus() {
        OrderTable orderTable = new OrderTableBuilder()
                .setId(1L)
                .setEmpty(false)
                .build()
                ;

        //when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        //when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(),
                //Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("손님이 왔을때 인원수에 맞게 테이블 정보 변경")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5})
    void changeNumberOfGuests(int value) {
        OrderTable requestOrderTable = new OrderTableBuilder()
                .setId(1L)
                .setNumberOfGuests(value)
                .setEmpty(false)
                .build()
                ;

        OrderTable savedOrderTable = new OrderTableBuilder()
                .setId(1L)
                .setNumberOfGuests(value)
                .setEmpty(false)
                .build()
                ;

        orderTableDao.save(requestOrderTable);

       // when(orderTableDao.findById(requestOrderTable.getId())).thenReturn(Optional.of(requestOrderTable));
        //when(orderTableDao.save(requestOrderTable)).thenReturn(savedOrderTable);

        OrderTable changedOrderTable = tableBo.changeNumberOfGuests(requestOrderTable.getId(), requestOrderTable);

        assertThat(changedOrderTable.getId()).isEqualTo(savedOrderTable.getId());
        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(savedOrderTable.getNumberOfGuests());
    }

    @DisplayName("인원수가 0보다 작을 시 에러")
    @ParameterizedTest
    @ValueSource(ints = {-1, -5})
    void changeNumberOfGuestsFailByNumberLessThanZero(int value) {
        OrderTable orderTable = new OrderTableBuilder()
                .setId(1L)
                .setNumberOfGuests(value)
                .setEmpty(false)
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("등록되어 있지 않은 테이블 정보 변경시 에러")
    @Test
    void changeNumberOfGuestsFailByNotExistTable() {
        OrderTable orderTable = new OrderTableBuilder()
                .setId(1L)
                .setNumberOfGuests(3)
                .setEmpty(false)
                .build()
                ;

        //when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }
}
