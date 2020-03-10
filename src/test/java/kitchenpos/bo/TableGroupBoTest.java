package kitchenpos.bo;

import kitchenpos.TestFixture;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @DisplayName("테이블 그룹 정상 생성")
    @Test
    void create() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        List<OrderTable> orderTables = tableGroup.getOrderTables();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        assertAll(
                () -> assertThat(savedTableGroup.getId()).isEqualTo(tableGroup.getId()),
                () -> assertThat(savedTableGroup.getOrderTables()).containsAll(tableGroup.getOrderTables())
        );
    }

    @DisplayName("테이블이 하나일때 그룹 생성 에러")
    @Test
    void createFailByTableLessTwo() {
        TableGroup tableGroup = TestFixture.generateTableGroupHasOneOrderTable();

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 없을때 그룹 생성 에러")
    @Test
    void createFailByTableNotExist() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();
        tableGroup.setOrderTables(null);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("등록되지 않은 테이블로 그룹 생성시 에러")
    @Test
    void createFailByTableNotSaved() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        List<OrderTable> orderTables = tableGroup.getOrderTables();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(Arrays.asList());

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("그룹화 되어 있는 테이블을 그룹화 시도시 에러")
    @Test
    void createFailByTableGroupExist() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        List<OrderTable> orderTables = tableGroup.getOrderTables();

        orderTables.stream()
                .forEach(orderTable -> orderTable.setTableGroupId(2L));

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 정상 삭제")
    @Test
    void delete() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        List<OrderTable> orderTables = tableGroup.getOrderTables();

        orderTables.stream()
                .forEach(orderTable -> orderTable.setTableGroupId(tableGroup.getId()));

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(any())).willReturn(any());

        tableGroupBo.delete(tableGroup.getId());

        verify(orderTableDao, times(orderTables.size())).save(any());
    }

    @DisplayName("주문 상태가 요리 중 이거나 식사 중인 테이블은 삭제시 에러")
    @Test
    void deleteFailByStatus() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        List<OrderTable> orderTables = tableGroup.getOrderTables();

        orderTables.stream()
                .forEach(orderTable -> orderTable.setTableGroupId(tableGroup.getId()));

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(tableGroup.getId()));
    }
}
