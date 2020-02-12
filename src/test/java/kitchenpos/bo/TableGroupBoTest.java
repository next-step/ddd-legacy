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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        assertThat(savedTableGroup.getId()).isEqualTo(tableGroup.getId());
        assertThat(savedTableGroup.getOrderTables()).containsAll(tableGroup.getOrderTables());
    }

    @DisplayName("테이블이 하나일때 그룹 생성 에러")
    @Test
    void createFailByTableLessTwo() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 없을때 그룹 생성 에러")
    @Test
    void createFailByTableNotExist() {
        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setCreatedDate(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("등록되지 않은 테이블로 그룹 생성시 에러")
    @Test
    void createFailByTableNotSaved() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(Arrays.asList());

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("그룹화 되어 있는 테이블을 그룹화 시도시 에러")
    @Test
    void createFailByTableGroupExist() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setTableGroupId(2L)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(Arrays.asList());

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 정상 삭제")
    @Test
    void delete() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

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
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build();

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build();

        List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(tableGroup.getId()));
    }



    public static class TableGroupBuilder {
        private Long id;
        private LocalDateTime createdDate;
        private List<OrderTable> orderTables;

        public TableGroupBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public TableGroupBuilder setCreatedDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TableGroupBuilder setOrderTables(List<OrderTable> orderTables) {
            this.orderTables = orderTables;
            return this;
        }

        public TableGroup build() {
            TableGroup tableGroup = new TableGroup();

            tableGroup.setCreatedDate(createdDate);
            tableGroup.setId(id);
            tableGroup.setOrderTables(orderTables);

            return tableGroup;
        }
    }

    public static class OrderTableBuilder {
        private Long id;
        private Long tableGroupId;
        private int numberOfGuests;
        private boolean empty;

        public OrderTableBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public OrderTableBuilder setTableGroupId(Long tableGroupId) {
            this.tableGroupId = tableGroupId;
            return this;
        }

        public OrderTableBuilder setNumberOfGuests(int numberOfGuests) {
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public OrderTableBuilder setEmpty(boolean empty) {
            this.empty = empty;
            return this;
        }

        public OrderTable build() {
            OrderTable orderTable = new OrderTable();

            orderTable.setId(id);
            orderTable.setEmpty(empty);
            orderTable.setNumberOfGuests(numberOfGuests);
            orderTable.setTableGroupId(tableGroupId);

            return orderTable;
        }
    }
}