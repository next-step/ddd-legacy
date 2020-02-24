package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.ObjectEnumerableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName("테이블 그룹은 2개 이상의 테이블로 생성된다.")
    void createTableGroupWithSingleTableException() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofEmpty())
        );
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹은 빈 테이블로 생성된다.")
    void createTableGroupWithNotEmptyTableException() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstOfMulti(), OrderTableTest.ofSecondOfMulti())
        );
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹이 생성되면 테이블은 비어있지 않은 상태로 변경된다.")
    void createTableHavingNotEmptyStatus() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroupBo.create(tableGroup);

        assertThat(tableGroup.getOrderTables())
                .allSatisfy(orderTable -> assertThat(orderTable.isEmpty()).isFalse());
    }

    @Test
    @DisplayName("테이블 그룹은 없앨 수 있다.")
    void deleteTableGroup() {
        TableGroup tableGroup = TableGroupTest.ofTwoEmptyTable();
        tableGroup = tableGroupDao.save(tableGroup);

        tableGroupBo.delete(tableGroup.getId());

        assertThat(tableGroup.getOrderTables())
                .allSatisfy(orderTable -> assertThat(orderTable.getTableGroupId()).isNull());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"COOKING", "MEAL"})
    @DisplayName("요리중이거나 식사중인 테이블그룹은 없앨 수 없다.")
    void deleteWithCookingOrMealTableException(String orderStatus) {
        TableGroup tableGroup = TableGroupTest.ofTwoMultiTable();
        tableGroupDao.save(tableGroup);

        Order order = OrderTest.ofOneHalfAndHalfInTableGroup();
        order.setOrderStatus(orderStatus);
        orderDao.save(order);

        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.delete(tableGroup.getId()));

    }
}
