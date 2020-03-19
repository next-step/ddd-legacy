package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableGroupBoTest {

    private final TableGroupDao tableGroupDao = new InMemoryTableGroupDao();
    private final OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private final OrderDao orderDao = new InMemoryOrderDao();
    private final TableGroupBo tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);

    private OrderTable firstTable = OrderTableTest.ofFirstInTableGroup();
    private OrderTable secondTable = OrderTableTest.ofSecondInTableGroup();

    @BeforeEach
    void setUp() {

        orderTableDao.save(firstTable);
        orderTableDao.save(secondTable);
    }

    @Test
    @DisplayName("테이블 그룹은 2개 이상의 테이블로 생성된다.")
    void createTableGroupWithSingleTableException() {
        TableGroup tableGroup = TableGroupTest.of();
        tableGroup.setOrderTables(
                Collections.singletonList(OrderTableTest.ofEmpty())
        );
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹은 빈 테이블로 생성된다.")
    void createTableGroupWithNotEmptyTableException() {
        TableGroup tableGroup = TableGroupTest.of();

        firstTable.setEmpty(false);
        secondTable.setEmpty(false);
        tableGroup.setOrderTables(Arrays.asList(firstTable, secondTable));

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹이 생성되면 테이블은 비어있지 않은 상태로 변경된다.")
    void createTableHavingNotEmptyStatus() {
        TableGroup tableGroup = tableGroupBo.create(TableGroupTest.of());

        assertThat(tableGroup.getOrderTables())
                .allSatisfy(orderTable -> assertThat(orderTable.isEmpty()).isFalse());
    }

    @Test
    @DisplayName("테이블 그룹은 없앨 수 있다.")
    void deleteTableGroup() {
        TableGroup tableGroup = TableGroupTest.of();

        firstTable.setTableGroupId(tableGroup.getId());
        secondTable.setTableGroupId(tableGroup.getId());
        tableGroup.setOrderTables(Arrays.asList(firstTable, secondTable));
        tableGroup = tableGroupDao.save(TableGroupTest.of());

        tableGroupBo.delete(tableGroup.getId());

        assertThat(tableGroup.getOrderTables())
                .allSatisfy(orderTable -> assertThat(orderTable.getTableGroupId()).isNull());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"COOKING", "MEAL"})
    @DisplayName("요리중이거나 식사중인 테이블그룹은 없앨 수 없다.")
    void deleteWithCookingOrMealTableException(String orderStatus) {
        TableGroup tableGroup = tableGroupDao.save(TableGroupTest.of());

        Order orderForFirstTable = OrderTest.ofFirstInTableGroup();
        orderForFirstTable.setOrderStatus(orderStatus);
        orderDao.save(orderForFirstTable);

        Order orderForSecondTable = OrderTest.ofSecondInTableGroup();
        orderForSecondTable.setOrderStatus(orderStatus);
        orderDao.save(orderForSecondTable);

        firstTable.setTableGroupId(tableGroup.getId());
        secondTable.setTableGroupId(tableGroup.getId());
        tableGroup.setOrderTables(Arrays.asList(firstTable, secondTable));

        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.delete(tableGroup.getId()));
    }
}
