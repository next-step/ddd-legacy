package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableBoTest {

    private OrderDao orderDao = new InMemoryOrderDao();
    private OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private TableGroupDao tableGroupDao = new InMemoryTableGroupDao();

    private TableBo tableBo;

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @Test
    @DisplayName("테이블은 추가될 수 있다.")
    void createTest() {
        OrderTable orderTable = new OrderTable();
        OrderTable orderTableResult = tableBo.create(orderTable);
        assertAll(
                () -> assertThat(orderTableResult.getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(orderTableResult.getTableGroupId()).isEqualTo(orderTable.getTableGroupId()),
                () -> assertThat(orderTableResult.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests())
        );
    }

    @Test
    @DisplayName("테이블은 비어있는 상태로 변경할 수 있다.")
    void updateOrderTableEmptyTest() {
        OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTable = orderTableDao.save(orderTable);

        orderTable.setEmpty(true);
        orderTable = tableBo.changeEmpty(orderTable.getId(), orderTable);
        assertThat(orderTable.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    @DisplayName("주문의 상태가 요리중이거나 식사중인 테이블은 비울 수 없다.")
    void updateOrderTableEmptyWithCookingOrMealException(String orderStatus) {
        OrderTable orderTable = OrderTableTest.ofEmpty();

        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTableDao.save(orderTable);

        Order order = OrderTest.of();
        order.setOrderStatus(orderStatus);
        orderDao.save(order);

        orderTable.setEmpty(true);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("테이블 그룹이 있는 테이블은 비울 수 없다.")
    void updateOrderTableEmptyInTableGroupException() {

        TableGroup tableGroup = TableGroupTest.of();

        OrderTable firstTable = OrderTableTest.ofFirstInTableGroup();
        OrderTable secondTable = OrderTableTest.ofSecondInTableGroup();

        firstTable.setTableGroupId(tableGroup.getId());
        secondTable.setTableGroupId(tableGroup.getId());
        tableGroup.setOrderTables(Arrays.asList(firstTable, secondTable));

        orderTableDao.save(firstTable);
        orderTableDao.save(secondTable);

        firstTable.setEmpty(true);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(firstTable.getId(), firstTable));
    }

    @Test
    @DisplayName("테이블의 인원은 변경할 수 있다.")
    void updateGuestOfTableTest() {
        OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTableDao.save(orderTable);

        orderTable.setNumberOfGuests(2);
        orderTable = tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(2);
    }

    @Test
    @DisplayName("비어있는 테이블의 인원은 변경할 수 없다")
    void updateGuestInEmptyTableException() {
        OrderTable orderTable = orderTableDao.save(OrderTableTest.ofEmpty());

        orderTable.setNumberOfGuests(2);
        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("전체 테이블 목록을 조회할 수 있다.")
    void readAllTableListTest() {
        OrderTable orderTable1 = orderTableDao.save(OrderTableTest.ofEmpty());
        OrderTable orderTable2 = orderTableDao.save(OrderTableTest.ofFirstInTableGroup());
        OrderTable orderTable3 = orderTableDao.save(OrderTableTest.ofSecondInTableGroup());
        assertThat(tableBo.list()).contains(orderTable1, orderTable2, orderTable3);
    }
}
