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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private OrderTable mockOrderTable1;
    private OrderTable mockOrderTable2;

    @BeforeEach
    void beforeEach() {
        mockOrderTable1 = new OrderTable();
        mockOrderTable1.setId(1L);
//        mockOrderTable1.setTableGroupId(1L);
        mockOrderTable1.setNumberOfGuests(5);
        mockOrderTable1.setEmpty(true);

        mockOrderTable2 = new OrderTable();
        mockOrderTable2.setId(2L);
//        mockOrderTable2.setTableGroupId(1L);
        mockOrderTable2.setNumberOfGuests(10);
        mockOrderTable2.setEmpty(true);
    }

    @DisplayName("새로운 테이블그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        ArrayList<OrderTable> mockOrderTables = new ArrayList<>(Arrays.asList(mockOrderTable1, mockOrderTable2));
        TableGroup newTableGroup = new TableGroup();
        newTableGroup.setOrderTables(mockOrderTables);

        given(orderTableDao.findAllByIdIn(new ArrayList<Long>(Arrays.asList(mockOrderTable1.getId(), mockOrderTable2.getId()))))
                .willReturn(mockOrderTables);
        given(tableGroupDao.save(newTableGroup)).willAnswer(invocation -> {
            newTableGroup.setId(1L);
            newTableGroup.setCreatedDate(LocalDateTime.now());
            newTableGroup.getOrderTables().forEach(orderTable -> {
                orderTable.setEmpty(false);
            });
            return newTableGroup;
        });
        given(orderTableDao.save(any(OrderTable.class))).willReturn(null);

        // when
        TableGroup result = tableGroupBo.create(newTableGroup);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCreatedDate()).isNotNull();
        assertThat(result.getOrderTables().size()).isEqualTo(newTableGroup.getOrderTables().size());
        assertThat(result.getOrderTables().get(0).getId()).isEqualTo(newTableGroup.getOrderTables().get(0).getId());

        ArgumentCaptor<OrderTable> argument = ArgumentCaptor.forClass(OrderTable.class);
        verify(orderTableDao, times(mockOrderTables.size())).save(argument.capture());
        assertThat(argument.getValue().getTableGroupId()).isEqualTo(1L);
        assertThat(argument.getValue().isEmpty()).isEqualTo(false); // 지정한 테이블의 공석여부를 아님으로 설정한다.
    }

    @DisplayName("새로운 테이블그룹을 생성 시, 테이블그룹 내 테이블은 2개 이상이다.")
    @ParameterizedTest
    @MethodSource(value = "provideLessOrderTables")
    void createOnlyWhenTablesAreAtLeast2(List<OrderTable> orderTables) {
        // given
        TableGroup newTableGroup = new TableGroup();
        newTableGroup.setOrderTables(orderTables);

        // when
        // then
        assertThatThrownBy(() -> {
            tableGroupBo.create(newTableGroup);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<List> provideLessOrderTables() {
        return Stream.of(
                null,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(new OrderTable()))
        );
    }

    @DisplayName("테이블그룹 생성 시, 지정한 테이블이 공석이여야한다.")
    @ParameterizedTest
    @MethodSource(value = "provideNotEmptyOrderTables")
    void createOnlyWhenTablesAreEmpty(List<OrderTable> orderTables) {
        // given
        TableGroup newTableGroup = new TableGroup();
        newTableGroup.setOrderTables(orderTables);

        given(orderTableDao.findAllByIdIn(orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList())))
                .willReturn(orderTables);

        // when
        // then
        assertThatThrownBy(() -> {
            tableGroupBo.create(newTableGroup);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<List> provideNotEmptyOrderTables() {
        OrderTable emptyOrderTable = new OrderTable();
        emptyOrderTable.setId(1L);
        emptyOrderTable.setEmpty(true);

        OrderTable notEmptyOrderTable1 = new OrderTable();
        notEmptyOrderTable1.setId(2L);
        notEmptyOrderTable1.setEmpty(false);

        OrderTable notEmptyOrderTable2 = new OrderTable();
        notEmptyOrderTable2.setId(3L);
        notEmptyOrderTable2.setEmpty(false);

        return Stream.of(
                new ArrayList<>(Arrays.asList(emptyOrderTable, notEmptyOrderTable1)),
                new ArrayList<>(Arrays.asList(notEmptyOrderTable1, notEmptyOrderTable2))
        );
    }

    @DisplayName("테이블그룹 생성 시, 지정한 테이블이 다른 테이블그룹에 포함되어있지 않아야한다.")
    @ParameterizedTest
    @MethodSource(value = "provideNotFreeOrderTables")
    void createOnlyWhenTablesAreNotIncludedInTableGroup(List<OrderTable> orderTables) {
        // given
        TableGroup newTableGroup = new TableGroup();
        newTableGroup.setOrderTables(orderTables);

        given(orderTableDao.findAllByIdIn(orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList())))
                .willReturn(orderTables);

        // when
        // then
        assertThatThrownBy(() -> {
            tableGroupBo.create(newTableGroup);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<List> provideNotFreeOrderTables() {
        OrderTable freeOrderTable = new OrderTable();
        freeOrderTable.setId(1L);
        freeOrderTable.setEmpty(true);

        OrderTable notFreeOrderTable1 = new OrderTable();
        notFreeOrderTable1.setId(2L);
        notFreeOrderTable1.setTableGroupId(1L);
        notFreeOrderTable1.setEmpty(true);

        OrderTable notFreeOrderTable2 = new OrderTable();
        notFreeOrderTable2.setId(3L);
        notFreeOrderTable1.setTableGroupId(2L);
        notFreeOrderTable2.setEmpty(true);

        return Stream.of(
                new ArrayList<>(Arrays.asList(freeOrderTable, notFreeOrderTable1)),
                new ArrayList<>(Arrays.asList(notFreeOrderTable1, notFreeOrderTable2))
        );
    }

    @DisplayName("테이블그룹을 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        mockOrderTable1.setTableGroupId(1L);
        mockOrderTable2.setTableGroupId(1L);
        ArrayList<OrderTable> mockOrderTables = new ArrayList<>(Arrays.asList(mockOrderTable1, mockOrderTable2));

        given(orderTableDao.findAllByTableGroupId(1L)).willReturn(mockOrderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(mockOrderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class))).willReturn(null);

        // when
        tableGroupBo.delete(1L);

        // then
        ArgumentCaptor<OrderTable> argument = ArgumentCaptor.forClass(OrderTable.class);
        verify(orderTableDao, times(mockOrderTables.size())).save(argument.capture());
        assertThat(argument.getValue().getTableGroupId()).isNull(); // 테이블그룹에 지정되었던 테이블들을 지정 해제한다.
    }

    @DisplayName("테이블그룹 삭제 시, 테이블그룹에 포함된 테이블들에서 발생한 모든 주문들의 주문상태가 완료인 경우에만 삭제할 수 있다.")
    @Test
    void deleteOnlyWhenOrdersAreCompleted() {
        // given
        mockOrderTable1.setTableGroupId(1L);
        mockOrderTable2.setTableGroupId(1L);
        ArrayList<OrderTable> mockOrderTables = new ArrayList<>(Arrays.asList(mockOrderTable1, mockOrderTable2));

        given(orderTableDao.findAllByTableGroupId(1L)).willReturn(mockOrderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(mockOrderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            tableGroupBo.delete(1L);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
