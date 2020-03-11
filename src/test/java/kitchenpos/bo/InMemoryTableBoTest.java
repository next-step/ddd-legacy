package kitchenpos.bo;

import kitchenpos.dao.InMemoryOrderDao;
import kitchenpos.dao.InMemoryOrderTableDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.support.OrderBuilder;
import kitchenpos.support.OrderTableBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InMemoryTableBoTest {

    private OrderDao orderDao = new InMemoryOrderDao();
    private OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private TableBo tableBo;

    @BeforeEach
    void setup (){
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @DisplayName("입력한 주문테이블을 저장한다.")
    @Test
    void create (){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(2)
            .empty(false)
            .build();

        OrderTable savedOrderTable = tableBo.create(orderTable);

        assertThat(savedOrderTable).isEqualToComparingFieldByField(orderTable);
    }

    @DisplayName("이미 ID가 있는경우 정보를 update 한다.")
    @Test
    void createWithoutId(){
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(2)
            .empty(false)
            .build();

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(2L)
            .numberOfGuests(3)
            .empty(false)
            .build();
    }

    @DisplayName("주문테이블 목록을 가져온다.")
    @Test
    void list (){
        List<OrderTable> orderTables = new ArrayList<>();
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(3)
            .empty(false)
            .build();
        orderTables.add(orderTable1);
        tableBo.create(orderTable1);

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(2L)
            .tableGroupId(1L)
            .numberOfGuests(3)
            .empty(false)
            .build();
        orderTables.add(orderTable2);
        tableBo.create(orderTable2);

        List<OrderTable> savedList = tableBo.list();

        assertThat(savedList).isEqualTo(orderTables);
    }

    @DisplayName("잘못된 OrderTable를 입력하면 IllegalArgumentException 이 발생한다.")
    @Test
    void changeEmptyWithWrongOrderTableId(){
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(3)
            .empty(false)
            .build();
        tableBo.create(orderTable1);

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(2L)
            .tableGroupId(1L)
            .numberOfGuests(3)
            .empty(false)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(orderTable2.getId(), orderTable2));
    }

    @DisplayName("OrderTable의 TableGroupId이 설정되어있으면 안된다.")
    @Test
    void changeEmptyTableGroup (){
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(3)
            .empty(false)
            .build();
        tableBo.create(orderTable1);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(orderTable1.getId(), orderTable1));
    }

    @DisplayName("OrderStatus가 COOKING 이거나 MEAL 이면 상태를 Empty로 변경 할 수 없다.")
    @ValueSource(strings = {"COOKING", "MEAL"})
    @ParameterizedTest
    void changeEmptyTableGroupOrderStatusIsNotCompletion (final String status){
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(null)
            .numberOfGuests(3)
            .empty(false)
            .build();
        tableBo.create(orderTable1);

        Order order = new OrderBuilder()
            .id(1L)
            .orderTableId(orderTable1.getId())
            .orderStatus(status)
            .orderedTime(LocalDateTime.now())
            .orderLineItems(Collections.emptyList())
            .build();
        orderDao.save(order);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(order.getOrderTableId(), orderTable1));
    }

    @DisplayName("savedOrderTable의 상태를 비어있음 으로 변경한다.")
    @Test
    void changeEmpty(){
        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(null)
            .numberOfGuests(3)
            .empty(true)
            .build();
        tableBo.create(orderTable1);

        Order order = new OrderBuilder()
            .id(1L)
            .orderTableId(orderTable1.getId())
            .orderStatus("COMPLETION")
            .orderedTime(LocalDateTime.now())
            .orderLineItems(Collections.emptyList())
            .build();
        orderDao.save(order);

        OrderTable updatedOrderTable = tableBo.changeEmpty(orderTable1.getId(), orderTable1);
        assertThat(updatedOrderTable.isEmpty()).isEqualTo(true);
    }

    @DisplayName("변경 후 손님의 수가 0보다 작으면 안된다.")
    @ValueSource(ints = {-1, -2, -3})
    @ParameterizedTest
    void changeNumberOfGuestsIsNegativeNumber(final int numberOfGuest){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(numberOfGuest)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("저장된 OrderTable이 없으면 안된다.")
    @Test
    void changeNumberOfGuestOrderTableIsNotExist (){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(2)
            .build();
        tableBo.create(orderTable);

        OrderTable inputOrderTable = new OrderTableBuilder()
            .id(2L)
            .numberOfGuests(2)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(inputOrderTable.getId(), inputOrderTable));

    }

    @DisplayName("저장되어있는 OrderTable이 이미 비어있으면 안된다.")
    @Test
    void changeNumberOfGuestOrderTableisEmpty (){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(2)
            .empty(true)
            .build();
        tableBo.create(orderTable);

        OrderTable inputOrderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(4)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(inputOrderTable.getId(), inputOrderTable));
    }

    @DisplayName("손님의 수를 변경한다.")
    @Test
    void changeNumberOfGuest(){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(2)
            .empty(false)
            .build();
        tableBo.create(orderTable);

        OrderTable inputOrderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(4)
            .build();

        OrderTable savedOrderTable = tableBo.changeNumberOfGuests(inputOrderTable.getId(), inputOrderTable);

        assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(inputOrderTable.getNumberOfGuests());
    }

}
