package kitchenpos.bo;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("테이블 그룹에 속한 테이블은 2개 이상이다.")
    @Test
    void min2Table() {
        List<OrderTable> tables = Arrays.asList(mock(OrderTable.class));
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("존재하는 테이블만 그룹에 포함될수 있다.")
    @Test
    void notExistTable() {

        List<OrderTable> tables = Arrays.asList(mock(OrderTable.class), mock(OrderTable.class));
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(Collections.emptyList());

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("이용중인 테이블만 한개의 그룹에만 속할수 있다.")
    @Test
    void notEmptyTableHasGroup() {
        TableGroup tableGroup = createTableGroup();
        tableGroup.getOrderTables().forEach(orderTable -> orderTable.setEmpty(false));

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블은 한개의 그룹에만 속할수 있다.")
    @Test
    void tableHasOnlyOneGroup() {
        TableGroup tableGroup = createTableGroup();

        tableGroup.getOrderTables().forEach(orderTable -> orderTable.setTableGroupId(1L));

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 그룹에 포함되면 이용중인 상태가 된다.")
    @Test
    void tableEmptyFalse() {
        TableGroup tableGroup = createTableGroup();

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        tableGroupBo.create(tableGroup).getOrderTables()
            .forEach(orderTable -> Assertions.assertThat(orderTable.isEmpty()).isEqualTo(false));
    }

    @DisplayName("주문의 상태가 완료일 경우만 테이블 그룹에서 테이블을 제거 할수 있다.")
    @Test
    void onlyCompleteStatusCanDelete() {

        List<OrderTable> orderTables = createTableGroup().getOrderTables();

        given(orderTableDao.findAllByTableGroupId(anyLong())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
            .willReturn(true);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.delete(1L));
    }

    @DisplayName("테이블 그룹에서 테이블을 제외한다.")
    @Test
    void delete() {
        List<OrderTable> orderTables = createTableGroup().getOrderTables();

        given(orderTableDao.findAllByTableGroupId(anyLong())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList())).willReturn(false);

        tableGroupBo.delete(anyLong());

        orderTables.forEach(orderTable -> Assertions.assertThat(orderTable.getTableGroupId()).isNull());
    }

    private TableGroup createTableGroup() {
        OrderTable table1 = new OrderTable();
        table1.setEmpty(true);
        table1.setTableGroupId(null);

        OrderTable table2 = new OrderTable();
        table2.setEmpty(true);
        table2.setTableGroupId(null);

        List<OrderTable> tables = Arrays.asList(table1, table2);
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        return tableGroup;
    }

}
