package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static kitchenpos.bo.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {
    @Mock
    private TableGroupDao tableGroupDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    private TableGroupBo tableGroupBo;

    private TableGroup tableGroup;
    private List<OrderTable> orderTableList;
    private OrderTable orderTable1;
    private OrderTable orderTable2;

    @BeforeEach
    void setUp() {
        tableGroup = new TableGroup();
        orderTableList = new ArrayList<>();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTableList);

        orderTable1 = new OrderTable();
        orderTable1.setId(1L);
        orderTable1.setNumberOfGuests(1);
        orderTable1.setEmpty(true);
        orderTableList.add(orderTable1);

        orderTable2 = new OrderTable();
        orderTable2.setId(2L);
        orderTable2.setNumberOfGuests(1);
        orderTable2.setEmpty(true);
        orderTableList.add(orderTable2);

        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);
    }

    @DisplayName("테이블 그룹을 만들수 있다.")
    @Test
    void create() {
        // given
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTableList);
        given(tableGroupDao.save(any(TableGroup.class))).willReturn(tableGroup);

        // when
        TableGroup actual = tableGroupBo.create(tableGroup);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("테이블 그룹은 2개 이상 테이블로 되어있다.")
    @Test
    void tableMoreThanTwo() {
        final OrderTable orderTable = emptyTable();
        List<OrderTable> tables = Collections.singletonList(orderTable);
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(tables);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 생성시 이미 사용중이면 안된다.")
    @Test
    void createTableGroupWithEmptyTable() {
        // given
        List<OrderTable> expectedTables = Arrays.asList(orderTable1(), orderTable2());
        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(expectedTables);
        tableGroup.setOrderTables(expectedTables);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 생성시 이미 테이블 그룹에 속해있으면 안된다.")
    @Test
    void createTableGroupWithEmptyTableGroup() {
        orderTableDao.save(groupedTable1());

        final TableGroup expected = tableGroup();
        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(expected));
    }

    @DisplayName("테이블 그룹 생성시 테이블은 사용 상태로 바뀐다.")
    @Test
    void createTableStatusNotEmpty() {
        // given
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTableList);
        given(tableGroupDao.save(any(TableGroup.class))).willReturn(tableGroup);

        // when
        TableGroup actual = tableGroupBo.create(tableGroup);

        // then
        assertThat(actual.getOrderTables().get(0).isEmpty()).isFalse();
    }

    @DisplayName("테이블 그룹을 삭제시 해당 테이블 그룹이 사라진다.")
    @Test
    void deleteTableGroup() {
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTableList);

        // when
        tableGroupBo.delete(tableGroup.getId());

        // then
        assertAll(
                () -> assertThat(tableGroup.getOrderTables().get(0).getTableGroupId()).isNull(),
                () -> assertThat(tableGroup.getOrderTables().get(1).getTableGroupId()).isNull()
        );
    }

    @DisplayName("테이블 그룹을 삭제시 완료 상태여야 한다.")
    @Test
    void deleteTableGroupShouldComplete() {
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTableList);

        // when
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .thenReturn(true);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(tableGroup.getId()));
    }
}
