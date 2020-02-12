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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {
    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @Mock private TableGroupDao tableGroupDao;
    @InjectMocks private TableGroupBo tableGroupBo;

    private TableGroup tableGroup;
    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private List<OrderTable> orderTableList;

    @BeforeEach
    void setup() {
        this.orderTable1 = new OrderTable();
        this.orderTable1.setId(1L);
        this.orderTable1.setTableGroupId(null);
        this.orderTable1.setEmpty(true);
        this.orderTable1.setNumberOfGuests(2);

        this.orderTable2 = new OrderTable();
        this.orderTable2.setId(2L);
        this.orderTable2.setTableGroupId(null);
        this.orderTable2.setEmpty(true);
        this.orderTable2.setNumberOfGuests(2);

        this.orderTableList = new ArrayList<>();
        this.orderTableList.add(orderTable1);
        this.orderTableList.add(orderTable2);

        this.tableGroup = new TableGroup();
        this.tableGroup.setId(1L);
        this.tableGroup.setCreatedDate(LocalDateTime.now());
        this.tableGroup.setOrderTables(orderTableList);

    }

    @DisplayName("테이블그룹을 생성할 수 있다.")
    @Test
    void create() {
        when(orderTableDao.findAllByIdIn(any(List.class))).thenReturn(this.orderTableList);
        when(tableGroupDao.save(any(TableGroup.class))).thenReturn(this.tableGroup);
        when(orderTableDao.save(any(OrderTable.class))).thenReturn(this.orderTable1);
        TableGroup result = tableGroupBo.create(this.tableGroup);
        assertThat(result.getId()).isEqualTo(this.tableGroup.getId());
    }

    @DisplayName("주문이 없는 테이블은 테이블그룹으로 생성할 수 없다.")
    @Test
    void createWithoutOrder() {
        this.tableGroup.setOrderTables(null);
        Throwable thrown = catchThrowable(() ->{
            tableGroupBo.create(this.tableGroup);
        });

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 경우에도 두개 이상의 테이블만 테이블그룹으로 생성할 수 있다.")
    @Test
    void createOverTwoTables() {
        this.orderTableList.remove(orderTable1);
        Throwable thrown = catchThrowable(() ->{
            tableGroupBo.create(this.tableGroup);
        });

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 테이블수와 생성하려는 테이블수가 같아야 한다.")
    @Test
    void createNumberOfTable() {
        List<OrderTable> savedOrderTableList = new ArrayList<>();
        savedOrderTableList.add(this.orderTable1);
        when(orderTableDao.findAllByIdIn(any(List.class))).thenReturn(savedOrderTableList);
        Throwable thrown = catchThrowable(() ->{
            tableGroupBo.create(this.tableGroup);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹을 생성하는 대상 테이블은 비어있어야 한다.")
    @Test
    void createEmptyTable() {
        this.orderTable1.setEmpty(false);
        Throwable thrown = catchThrowable(() ->{
            tableGroupBo.create(this.tableGroup);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹을 삭제할 수 있다.")
    @Test
    void delete() {
        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(this.orderTableList);
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(any(List.class),any(List.class)))
                .thenReturn(false);
        tableGroupBo.delete(this.tableGroup.getId());
        verify(orderTableDao,times(2))
                .save(any(OrderTable.class));
    }

    @DisplayName("테이블상태가 조리중: COOKING, 고객이 식사중인 주문: MEAL 인 경우는 삭제할 수 없다.")
    @Test
    void deleteByStatus() {
        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(this.orderTableList);
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(any(List.class),any(List.class)))
                .thenReturn(true);
        Throwable thrown = catchThrowable(() ->{
            tableGroupBo.delete(this.tableGroup.getId());
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
