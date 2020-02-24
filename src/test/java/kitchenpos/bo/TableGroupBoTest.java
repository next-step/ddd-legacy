package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableGroupBoTest {

    private TableGroupDao tableGroupDao = new InMemoryTableGroupDao();
    private OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private OrderDao orderDao = new InMemoryOrderDao();
    private TableGroupBo tableGroupBo;

    @BeforeEach
    void setUp() {
        orderTableDao.save(OrderTableTest.ofEmpty());
        orderTableDao.save(OrderTableTest.ofAnotherEmpty());

        orderTableDao.save(OrderTableTest.ofFirstOfMulti());
        orderTableDao.save(OrderTableTest.ofSecondOfMulti());

        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);
    }

    @Test
    @DisplayName("테이블 그룹은 2개 이상의 빈 테이블로 생성된다.")
    void createTableGroupTest() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroupBo.create(tableGroup);

        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstOfMulti(), OrderTableTest.ofSecondOfMulti())
        );
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));

        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofEmpty())
        );
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹이 생성되면 테이블은 비어있지 않은 상태로 변경된다.")
    void createTableHavingNotEmptyStatus() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroup = tableGroupBo.create(tableGroup);

        tableGroup.getOrderTables()
                .forEach(orderTable -> assertThat(orderTable.isEmpty()).isFalse());
    }

    @Test
    @DisplayName("테이블 그룹은 없앨 수 있다.")
    void deleteTableGroup() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroup = tableGroupBo.create(tableGroup);

        tableGroupBo.delete(tableGroup.getId());
        tableGroup.getOrderTables()
                .forEach(orderTable -> assertThat(orderTable.getTableGroupId()).isNull());
    }

    @Test
    @DisplayName("요리중이거나 식사중인 테이블그룹은 없앨 수 없다.")
    void deleteWithCookingOrMealTableException() {
        TableGroup tableGroup = TableGroupTest.ofTwoMultiTable();
        tableGroupDao.save(tableGroup);

        Order order = OrderTest.ofOneHalfAndHalfInTableGroup();
        order.setOrderStatus(OrderStatus.COOKING.toString());
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.delete(tableGroup.getId()));

        order.setOrderStatus(OrderStatus.MEAL.toString());
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.delete(tableGroup.getId()));
    }
}
