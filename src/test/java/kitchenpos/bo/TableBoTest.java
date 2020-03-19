package kitchenpos.bo;

import kitchenpos.dao.InMemoryOrderDao;
import kitchenpos.dao.InMemoryOrderTableDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableBoTest {
    private final OrderDao orderDao = new InMemoryOrderDao();
    private final OrderTableDao orderTableDao = new InMemoryOrderTableDao();

    private final TableBo tableBo = new TableBo(orderDao, orderTableDao);

    @Test
    @DisplayName("테이블은 추가될 수 있다.")
    void createTest() {
        final OrderTable orderTable = new OrderTable();
        final OrderTable orderTableResult = tableBo.create(orderTable);
        assertAll(
                () -> assertThat(orderTableResult.getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(orderTableResult.getTableGroupId()).isEqualTo(orderTable.getTableGroupId()),
                () -> assertThat(orderTableResult.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests())
        );
    }

    @Test
    @DisplayName("테이블은 비어있는 상태로 변경할 수 있다.")
    void updateOrderTableEmptyTest() {
        final OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);

        final OrderTable orderTableResult = orderTableDao.save(orderTable);

        orderTable.setEmpty(true);

        final OrderTable emptyOrderTable = tableBo.changeEmpty(orderTableResult.getId(), orderTable);
        assertThat(emptyOrderTable.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    @DisplayName("주문의 상태가 요리중이거나 식사중인 테이블은 비울 수 없다.")
    void updateOrderTableEmptyWithCookingOrMealException(String orderStatus) {
        final Order order = OrderTest.of();
        order.setOrderStatus(orderStatus);
        orderDao.save(order);

        final OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        final OrderTable orderTableResult = orderTableDao.save(orderTable);

        orderTable.setEmpty(true);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTableResult.getId(), orderTable));
    }

    @Test
    @DisplayName("테이블 그룹이 있는 테이블은 비울 수 없다.")
    void updateOrderTableEmptyInTableGroupException() {
        final TableGroup tableGroup = TableGroupTest.of();

        final OrderTable firstTable = OrderTableTest.ofFirstInTableGroup();
        final OrderTable secondTable = OrderTableTest.ofSecondInTableGroup();

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
        final OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);

        OrderTable orderTableResult = orderTableDao.save(orderTable);
        orderTable.setNumberOfGuests(2);

        orderTableResult = tableBo.changeNumberOfGuests(orderTableResult.getId(), orderTable);

        assertThat(orderTableResult.getNumberOfGuests()).isEqualTo(2);
    }

    @Test
    @DisplayName("비어있는 테이블의 인원은 변경할 수 없다")
    void updateGuestInEmptyTableException() {
        final OrderTable orderTable = OrderTableTest.ofEmpty();
        final OrderTable orderTableResult = orderTableDao.save(orderTable);

        orderTable.setNumberOfGuests(2);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeNumberOfGuests(orderTableResult.getId(), orderTable));
    }

    @Test
    @DisplayName("전체 테이블 목록을 조회할 수 있다.")
    void readAllTableListTest() {
        final OrderTable orderTable1 = orderTableDao.save(OrderTableTest.ofEmpty());
        final OrderTable orderTable2 = orderTableDao.save(OrderTableTest.ofFirstInTableGroup());
        final OrderTable orderTable3 = orderTableDao.save(OrderTableTest.ofSecondInTableGroup());
        assertThat(tableBo.list()).contains(orderTable1, orderTable2, orderTable3);
    }
}
