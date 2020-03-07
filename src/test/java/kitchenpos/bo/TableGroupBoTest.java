package kitchenpos.bo;

import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.builder.TableGroupBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

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
    private TableGroupBuilder tableGroupBuilder = new TableGroupBuilder();
    private OrderTableBuilder orderTableBuilder = new OrderTableBuilder();

    @Test
    @DisplayName("등록이 되면 모든 테이블은 사용중 상태로 되고, 같은 테이블그룹 번호를 가지게 된다")
    void create() {
        List<OrderTable> orderTableList = newArrayList();

        orderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());
        orderTableList.add(orderTableBuilder
                .id(2L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());

        TableGroup tableGroup = tableGroupBuilder
                .id(1L)
                .createdDate(LocalDateTime.now())
                .orderTables(orderTableList)
                .build();

        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(tableGroup.getOrderTables());

        given(tableGroupDao.save(tableGroup))
                .willReturn(tableGroup);

        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(any(OrderTable.class));

        TableGroup expectedTableGroup = tableGroupBo.create(tableGroup);

        assertThat(expectedTableGroup)
                .isEqualToComparingFieldByField(tableGroup);
        assertThat(expectedTableGroup.getOrderTables())
                .extracting("isEmpty")
                .contains(Boolean.FALSE);

        Long expectedTableGroupId = expectedTableGroup.getOrderTables().get(0).getTableGroupId();

        assertThat(expectedTableGroup.getOrderTables())
                .extracting("getTableGroupId")
                .containsOnly(expectedTableGroupId);
    }

    @Test
    @DisplayName("테이블그룹에 속한 테이블 갯수는 2 이상이어야 한다")
    void createGroupHasMoreThanTwoTables() {
        List<OrderTable> orderTableList = newArrayList();

        orderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());

        TableGroup tableGroup = tableGroupBuilder
                .id(1L)
                .createdDate(LocalDateTime.now())
                .orderTables(orderTableList)
                .build();

        assertThatThrownBy(() -> tableGroupBo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("그룹에 속한 모든 테이블은 비어있는 상태이고 다른 테이블그룹에 지정되어있지 않아야 한다")
    void createGroupTableIsEmptyAndNoGroups() {
        List<OrderTable> orderTableList = newArrayList();
        List<OrderTable> savedOrderTableList = newArrayList();

        savedOrderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .numberOfGuests(0)
                .build());
        savedOrderTableList.add(orderTableBuilder
                .id(2L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());

        orderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());
        orderTableList.add(orderTableBuilder
                .id(2L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build());

        TableGroup tableGroup = tableGroupBuilder
                .id(1L)
                .createdDate(LocalDateTime.now())
                .orderTables(orderTableList)
                .build();

        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(savedOrderTableList);


        assertThatThrownBy(() -> tableGroupBo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블그룹 번호를 지정하여 테이블그룹을 해제할 수 있다")
    void delete() {
        Long tableGroupID = 1L;
        List<OrderTable> orderTableList = newArrayList();

        orderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .tableGroupId(tableGroupID)
                .numberOfGuests(0)
                .build());
        orderTableList.add(orderTableBuilder
                .id(2L)
                .empty(Boolean.FALSE)
                .tableGroupId(tableGroupID)
                .numberOfGuests(0)
                .build());


        given(orderTableDao.findAllByTableGroupId(tableGroupID))
                .willReturn(orderTableList);

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(Boolean.FALSE);

        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(any(OrderTable.class));

        tableGroupBo.delete(tableGroupID);

        assertThat(orderTableList)
                .extracting("tableGroupId")
                .containsOnlyNulls();;

    }

    @Test
    @DisplayName("테이블그룹 속한 모든 테이블의 주문 상태가 \"COOKING\" 또는 \"MEAL\"이 아니어야 한다")
    void deleteExceptionInCookingOrMeal() {
        Long tableGroupID = 1L;
        List<OrderTable> orderTableList = newArrayList();

        orderTableList.add(orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .tableGroupId(tableGroupID)
                .numberOfGuests(0)
                .build());
        orderTableList.add(orderTableBuilder
                .id(2L)
                .empty(Boolean.FALSE)
                .tableGroupId(tableGroupID)
                .numberOfGuests(0)
                .build());

        given(orderTableDao.findAllByTableGroupId(tableGroupID))
                .willReturn(orderTableList);

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(Boolean.TRUE);

        assertThatThrownBy(() -> tableGroupBo.delete(tableGroupID))
                .isInstanceOf(IllegalArgumentException.class);
    }

}