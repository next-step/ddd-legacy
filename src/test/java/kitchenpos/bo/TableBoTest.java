package kitchenpos.bo;

import kitchenpos.dao.DefaultOrderDao;
import kitchenpos.dao.DefaultOrderTableDao;
import kitchenpos.dao.InMemoryOrderDao;
import kitchenpos.dao.InMemoryOrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableBoTest {

    DefaultOrderDao orderDao = new InMemoryOrderDao();
    DefaultOrderTableDao orderTableDao = new InMemoryOrderTableDao();

    TableBo tableBo;

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @Test
    @DisplayName("테이블은 추가될 수 있다.")
    void createTest() {
        OrderTable orderTable = new OrderTable();
        tableBo.create(orderTable);
    }

    @Test
    @DisplayName("테이블은 비어있는 상태로 변경할 수 있다.")
    void updateOrderTableEmptyTest() {
        OrderTable orderTable = OrderTableTest.ofSingle();
        orderTable = orderTableDao.save(orderTable);

        orderTable.setEmpty(true);
        tableBo.changeEmpty(orderTable.getId(), orderTable);

        assertThat(orderTable.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("주문의 상태가 요리중이거나 식사중인 테이블은 비울 수 없다.")
    void updateOrderTableEmptyWithCookingOrMealException() {
        OrderTable orderTable = OrderTableTest.ofSingle();
        orderTableDao.save(orderTable);

        orderTable.setEmpty(true);

        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order.setOrderStatus(OrderStatus.COOKING.toString());
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTable.getId(), orderTable));

        order.setOrderStatus(OrderStatus.MEAL.toString());
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("테이블 그룹이 있는 테이블은 비울 수 없다.")
    void updateOrderTableEmptyInTableGroupException() {

        OrderTable orderTable = OrderTableTest.ofFirstOfMulti();
        orderTableDao.save(orderTable);

        orderTable.setEmpty(true);

        Order order = OrderTest.ofOneHalfAndHalfInTableGroup();
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("테이블의 인원은 변경할 수 있다.")
    void updateGuestOfTableTest() {
        OrderTable orderTable = OrderTableTest.ofSingle();
        orderTableDao.save(orderTable);

        orderTable.setNumberOfGuests(2);
        orderTable = tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(2);
    }

    @Test
    @DisplayName("비어있는 테이블의 인원은 변경할 수 없다")
    void updateGuestInEmptyTableException() {
        OrderTable orderTable = OrderTableTest.ofSingle();
        orderTable.setEmpty(true);
        orderTableDao.save(orderTable);

        orderTable.setNumberOfGuests(2);
        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("전체 테이블 목록을 조회할 수 있다.")
    void readAllTableListTest() {
        OrderTable orderTable1 = orderTableDao.save(OrderTableTest.ofFirstOfMulti());
        OrderTable orderTable2 = orderTableDao.save(OrderTableTest.ofSecondOfMulti());
        assertThat(tableBo.list()).contains(orderTable1, orderTable2);
    }
}