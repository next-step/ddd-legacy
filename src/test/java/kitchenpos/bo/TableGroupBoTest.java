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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private TableGroup tableGroup;
    private List<OrderTable> orderTableList;
    OrderTable orderTable1;
    OrderTable orderTable2;
    OrderTable orderTable3;

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

        orderTable3 = new OrderTable();
        orderTable3.setId(3L);
        orderTable3.setNumberOfGuests(1);
        orderTable3.setEmpty(true);
        orderTableList.add(orderTable2);

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
        // given
        given(orderTableDao.findAllByIdIn(anyList()))
                .willThrow(IllegalArgumentException.class);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 생성시 이미 사용중이면 안된다.")
    @Test
    void createTableGroupWithEmptyTable() {
        // given
        orderTable1.setEmpty(false);
        orderTableList.add(orderTable1);
        tableGroup.setOrderTables(orderTableList);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 생성시 이미 테이블 그룹에 속해있으면 안된다.")
    @Test
    void createTableGroupWithEmptyTableGroup() {
        // given
        orderTableList.get(0).setTableGroupId(2L);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTableList);
        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
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
        assertThat(tableGroup.getOrderTables().get(0).getTableGroupId()).isNull();
        assertThat(tableGroup.getOrderTables().get(1).getTableGroupId()).isNull();
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
