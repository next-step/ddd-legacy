package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private TableGroup tableGroup;
    private List<OrderTable> orderTables;

    @BeforeEach
    void setUp() {
        orderTables = new ArrayList<>();
        orderTables.add(new OrderTable());
        orderTables.add(new OrderTable());
        orderTables.add(new OrderTable());
        orderTables.add(new OrderTable());
        orderTables.forEach(i -> i.setEmpty(true));

        tableGroup = new TableGroup();
        tableGroup.setOrderTables(orderTables);

        when(orderTableDao.findAllByIdIn(anyList())).thenReturn(orderTables);
        when(tableGroupDao.save(tableGroup)).thenReturn(tableGroup);
    }

    @DisplayName("테이블그룹을 생성할 수 있다.")
    @Test
    void create() {
        assertThat(tableGroupBo.create(tableGroup)).isEqualTo(tableGroup);
    }

    @DisplayName("테이블그룹의 테이블이 없가나 2개 이하인 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithLackOfOrderTables() {
        tableGroup.setOrderTables(new ArrayList<>());
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("존재하지 않는 테이블이 포함 된 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithNonExistOrderTables() {
        when(orderTableDao.findAllByIdIn(anyList())).thenThrow(IllegalArgumentException.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("`이용불가` 상태인 테이블이거나 이미 테이블그룹에 속해있는 테이블이 포함 된 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderTableStatus() {
        orderTables.get(0).setEmpty(false);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블그룹이 생성됨과 동시에 속한 테이블은 `이용불가` 의 상태를 가진다.")
    @Test
    void createdTableGroupStatus() {
        assertThat(tableGroupBo.create(tableGroup).getOrderTables().get(0).isEmpty()).isFalse();
    }

    @DisplayName("테이블그룹을 삭제할 수 있다.")
    @Test
    void delete() {
        when(orderTableDao.findAllByTableGroupId(anyLong())).thenReturn(orderTables);
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(
            anyList(),
            anyList())).thenReturn(false);

        tableGroupBo.delete(1L);
        assertThat(orderTables.get(0).getTableGroupId()).isNull();
    }

    @DisplayName("테이블그룹에 속한 테이블의 모든 주문이 `완료` 상태가 아닌 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderStatus() {
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(
            anyList(),
            anyList())).thenReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.delete(1L));
    }

    @DisplayName("테이블그룹이 삭제되면, 속한 모든 테이블들은 테이블그룹에서 제외된다.")
    @Test
    void deletedTableStatus() {
        tableGroupBo.delete(1L);
        assertThat(orderTables.get(0).getTableGroupId()).isNull();
    }
}
