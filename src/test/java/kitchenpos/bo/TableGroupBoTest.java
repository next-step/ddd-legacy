package kitchenpos.bo;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
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

    @DisplayName("테이블의 묶음은 2개이상이다.")
    @Test
    void min2Table() {
        OrderTable orderTable = createOrderTable(true, null);

        List<OrderTable> tables = Arrays.asList(orderTable);
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("존재하는 테이블만 묶일수 있다.")
    @Test
    void notExistTable() {
        //given
        OrderTable orderTable1 = createOrderTable(true, null);
        OrderTable orderTable2 = createOrderTable(true, null);

        List<OrderTable> tables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(Collections.emptyList());

        //when ,then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("미사용중인 테이블만 묶을수 있다.")
    @Test
    void notEmptyTableHasGroup() {
        //given
        OrderTable orderTable1 = createOrderTable(false, null);
        OrderTable orderTable2 = createOrderTable(false, null);

        List<OrderTable> tables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());

        //when ,then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("이미 묶어진 테이블은 다시 묶을수 없다.")
    @Test
    void tableHasOnlyOneGroup() {
        //given
        OrderTable orderTable1 = createOrderTable(true, 1L);
        OrderTable orderTable2 = createOrderTable(true, 1L);

        List<OrderTable> tables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());

        //when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("묶어진 테이블은 이용중인 상태이다.")
    @Test
    void tableEmptyFalse() {
        //given
        OrderTable orderTable1 = createOrderTable(true, null);
        OrderTable orderTable2 = createOrderTable(true, null);

        List<OrderTable> tables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByIdIn(anyList())).willReturn(tableGroup.getOrderTables());
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        tableGroupBo.create(tableGroup).getOrderTables()
            .forEach(orderTable -> Assertions.assertThat(orderTable.isEmpty()).isEqualTo(false));
    }



    @DisplayName("주문의 상태가 완료일 경우만 테이블 묶을을 해제할수있다.")
    @Test
    void onlyCompleteStatusCanDelete() {

        //given
        OrderTable orderTable1 = createOrderTable(true, 1L);
        OrderTable orderTable2 = createOrderTable(true, 1L);
        List<OrderTable> tables = Arrays.asList(orderTable1, orderTable2);
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        given(orderTableDao.findAllByTableGroupId(anyLong())).willReturn(tables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
            .willReturn(true);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.delete(1L));
    }

    @DisplayName("테이블 묶을을 해제 한다.")
    @Test
    void delete() {
        //given
        OrderTable orderTable1 = createOrderTable(true, 1L);
        OrderTable orderTable2 = createOrderTable(true, 1L);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        given(orderTableDao.findAllByTableGroupId(orderTable1.getTableGroupId()))
            .willReturn(orderTables);

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
            .willReturn(false);

        //when
        tableGroupBo.delete(orderTable1.getTableGroupId());

        //then
        orderTables
            .forEach(orderTable -> Assertions.assertThat(orderTable.getTableGroupId()).isNull());
    }

    private OrderTable createOrderTable(boolean isEmpty, Long tableGroupId) {
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setEmpty(isEmpty);
        orderTable1.setTableGroupId(tableGroupId);
        return orderTable1;
    }
}
