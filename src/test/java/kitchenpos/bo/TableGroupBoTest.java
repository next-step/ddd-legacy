package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
@ExtendWith(MockitoExtension.class)
class TableGroupBoTest extends Fixtures {

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @Test
    @DisplayName("테이블 그룹을 만들 수 있다.")
    void create() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);

        final OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(2L);
        orderTable1.setEmpty(true);

        final List<OrderTable> orderTables = Arrays.asList(orderTable, orderTable1);
        tableGroup.setOrderTables(orderTables);

        final List<Long> orderTableIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        final TableGroup result = tableGroupBo.create(tableGroup);

        assertThat(result).isNotNull();
        assertThat(result.getOrderTables().size()).isEqualTo(orderTables.size());
    }

    @Test
    @DisplayName("주문테이블이 1개 이상인 경우에 만들 수 있다.")
    void create_orderTable_validation() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);

        final List<OrderTable> orderTables = Arrays.asList(orderTable);
        tableGroup.setOrderTables(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("주문테이블의 사이즈와 동일해야 만들 수 있다.")
    void create_orderTable_size() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);


        final OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(2L);
        orderTable1.setEmpty(true);

        final List<OrderTable> orderTables = Arrays.asList(orderTable, orderTable1);
        tableGroup.setOrderTables(orderTables);

        final List<Long> orderTableIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(Arrays.asList(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블 그룹을 삭제 할 수 있다.")
    void delete() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTables);

        final List<Long> orderTableIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());

        final List<OrderTable> result = Fixtures.orderTables;
        result.forEach(res -> res.setTableGroupId(null));

        given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(Fixtures.orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(orderTableIds,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(Fixtures.orderTables.get(0))).willReturn(result.get(0));
        given(orderTableDao.save(Fixtures.orderTables.get(1))).willReturn(result.get(1));
        given(orderTableDao.save(Fixtures.orderTables.get(2))).willReturn(result.get(2));

        tableGroupBo.delete(tableGroup.getId());

        assertThat(result.get(0).getTableGroupId()).isNull();
        assertThat(result.get(1).getTableGroupId()).isNull();
        assertThat(result.get(2).getTableGroupId()).isNull();
    }

    @Test
    @DisplayName("주문 상태가 요리중이거나 먹고있으면 삭제 할 수 없다.")
    void delete_OrderStatus_cooking_or_meal() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTables);

        final List<Long> orderTableIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());

        final List<OrderTable> result = Fixtures.orderTables;
        result.forEach(res -> res.setTableGroupId(null));

        given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(Fixtures.orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(orderTableIds,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(tableGroup.getId()));
    }
}
