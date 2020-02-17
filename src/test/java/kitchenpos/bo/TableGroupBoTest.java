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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

    private TableGroup tableGroup = null;
    private List<OrderTable> orderTables = new ArrayList<>();

    @BeforeEach
    void setUp() {
        tableGroup = new TableGroup();
        tableGroup.setId(1L);
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setNumberOfGuests(3);
        orderTable1.setEmpty(true);
        orderTables.add(orderTable1);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setNumberOfGuests(3);
        orderTable2.setEmpty(true);
        orderTables.add(orderTable2);

        OrderTable orderTable3 = new OrderTable();
        orderTable3.setNumberOfGuests(3);
        orderTable3.setEmpty(true);
        orderTables.add(orderTable3);

        tableGroup.setOrderTables(orderTables);
    }

    @DisplayName("테이블 그룹을 만들수 있다.")
    @Test
    void createTableGroup() {
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTables);
        given(tableGroupDao.save(any(TableGroup.class))).willReturn(tableGroup);

        TableGroup actualTableGroup = tableGroupBo.create(tableGroup);
        assertThat(actualTableGroup).isEqualTo(tableGroup);
    }

    @DisplayName("테이블이 없거나, 테이블 수가 2개 이하이면 테이블 그룹을 만들 수 없다.")
    @Test
    void createTableGroup2() {
        orderTables.remove(1);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableGroupBo.create(tableGroup));

    }

    @DisplayName("테이블 수가 다를경우 그룹을 만들수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForDifferentSize() {
        List<OrderTable> lessOrderProducts = orderTables.subList(0, 2);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(lessOrderProducts);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("비어있지 않은 테이블이 있다면, 테이블 그룹을 만들수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForFullTable() {
        orderTables.get(0).setEmpty(false);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("이미 그룹지어진 테이블이 존재한다면, 테이블 그룹을 만들수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForExistTableGroup() {
        orderTables.get(0).setTableGroupId(1L);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹을 삭제한다.")
    @Test
    void deleteTableGroup() {
        given(orderTableDao.findAllByTableGroupId(
                anyLong())
        ).willReturn(
                orderTables
        );

        tableGroupBo.delete(tableGroup.getId());

    }


    @DisplayName("주문테이블의 주문상태가 cooking or meal 상태일경우 그룹을 삭제할수없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionUnderOrderStatusCookingOrMeal() {
        given(orderTableDao.findAllByTableGroupId(
                anyLong())
        ).willReturn(
                orderTables
        );

        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableGroupBo.delete(tableGroup.getId()));

    }
}
