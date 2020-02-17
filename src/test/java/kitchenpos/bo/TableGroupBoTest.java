package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MockitoSettings(strictness = Strictness.LENIENT)
class TableGroupBoTest {

    @Mock
    OrderDao orderDao;

    @Mock
    OrderTableDao orderTableDao;

    @Mock
    TableGroupDao tableGroupDao;

    @InjectMocks
    TableGroupBo tableGroupBo;
    private OrderTable table1;
    private OrderTable table2;
    private TableGroup tableGroup1;
    private TableGroup tableGroup2;

    @BeforeEach
    void setUp() {
        prepareFixtures();
        prepareMocks();
    }

    void prepareFixtures() {
        table1 = new OrderTable();
        table1.setId(1L);
        table1.setTableGroupId(null);
        table1.setNumberOfGuests(4);
        table1.setEmpty(true);

        table2 = new OrderTable();
        table2.setId(2L);
        table2.setTableGroupId(null);
        table2.setNumberOfGuests(3);
        table2.setEmpty(true);

        tableGroup1 = new TableGroup();
        tableGroup1.setId(1L);

        tableGroup2 = new TableGroup();
        tableGroup2.setId(2L);
    }

    void prepareMocks() {
        Mockito.when(
                orderTableDao.findAllByIdIn(Arrays.asList(table1.getId(), table2.getId()))
        ).thenReturn(Arrays.asList(table1, table2));

        Mockito.when(tableGroupDao.save(tableGroup1)).thenReturn(tableGroup1);

        Mockito.when(orderTableDao.save(table1)).thenReturn(table1);
        Mockito.when(orderTableDao.save(table2)).thenReturn(table2);
    }

    @DisplayName("사용자는 테이블 그룹을 등록할 수 있고, 등록이 완료되면 등록된 테이블 그룹 정보를 반환받아 확인할 수 있다")
    @Test
    void create() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));

        //when
        TableGroup actual = tableGroupBo.create(tableGroup1);
        List<OrderTable> tables = actual.getOrderTables();
        OrderTable actualTable1 = tables.get(0);
        OrderTable actualTable2 = tables.get(1);

        //then
        assertThat(actual).isEqualTo(tableGroup1);
        assertThat(tables.size()).isEqualTo(2);
        assertThat(tables).containsExactlyInAnyOrder(table1, table2);
        assertThat(actualTable1.isEmpty()).isFalse();
        assertThat(actualTable2.isEmpty()).isFalse();
        assertThat(actualTable1.getTableGroupId()).isNotNull();
        assertThat(actualTable2.getTableGroupId()).isNotNull();
    }

    @DisplayName("테이블 그룹에는 최소 2개의 테이블이 포함되어야 한다")
    @Test
    void create_minimum_table_count() {
        //given
        tableGroup1.setOrderTables(null);
        tableGroup2.setOrderTables(Collections.singletonList(table1));

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(tableGroup1);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            tableGroupBo.create(tableGroup2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 테이블이 테이블 그룹에 포함되어서는 안된다")
    @Test
    void create_with_unregistered_tables() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));

        Mockito.when(
                orderTableDao.findAllByIdIn(Arrays.asList(table1.getId(), table2.getId()))
        ).thenReturn(Collections.singletonList(table1));

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(tableGroup1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 그룹은 생성일자를 가진다")
    @Test
    void create_tableGroup_has_createDate() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));
        tableGroup1.setCreatedDate(null);

        //when
        TableGroup actual = tableGroupBo.create(tableGroup1);

        //then
        assertThat(actual.getCreatedDate()).isNotNull();
    }

    @DisplayName("이미 착석 중인 테이블은 등록하려는 테이블 그룹에 포함될 수 없다")
    @Test
    void create_already_not_empty() {
        //given
        table1.setEmpty(false);
        table2.setEmpty(false);
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(tableGroup1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("다른 테이블그룹에 포함된 테이블은 등록하려는 테이블 그룹에 포함될 수 없다")
    @Test
    void create_already_belongs_to_group() {
        //given
        table1.setTableGroupId(2L);
        table2.setTableGroupId(2L);
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(tableGroup1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 그룹이 생성될 때 포함되는 테이블은 착석 중 상태가 된다")
    @Test
    void create_tables_in_table_group_are_empty() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));
        //when
        TableGroup actual = tableGroupBo.create(tableGroup1);
        //then
        assertThat(actual.getOrderTables().get(0).isEmpty()).isFalse();
        assertThat(actual.getOrderTables().get(1).isEmpty()).isFalse();
    }

    @DisplayName("사용자는 테이블 그룹을 삭제할 수 있다")
    @Test
    void delete() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));
        TableGroup preparedTableGroup = tableGroupBo.create(tableGroup1);

        Mockito.when(orderTableDao.findAllByTableGroupId(preparedTableGroup.getId()))
                .thenReturn(Arrays.asList(table1, table2));
        Mockito.when(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                Arrays.asList(table1.getId(), table2.getId()),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())
        )).thenReturn(false);

        //when
        //then
        tableGroupBo.delete(preparedTableGroup.getId());
    }

    @DisplayName("테이블의 주문상태가 모두 '식사완료'인 경우에만 테이블 그룹을 삭제할 수 있다.")
    @Test
    void delete_completed_table() {
        //given
        tableGroup1.setOrderTables(Arrays.asList(table1, table2));
        TableGroup preparedTableGroup = tableGroupBo.create(tableGroup1);

        Mockito.when(orderTableDao.findAllByTableGroupId(preparedTableGroup.getId()))
                .thenReturn(Arrays.asList(table1, table2));
        Mockito.when(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                Arrays.asList(table1.getId(), table2.getId()),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())
        )).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.delete(preparedTableGroup.getId());
        }).isInstanceOf(IllegalArgumentException.class);
    }
}