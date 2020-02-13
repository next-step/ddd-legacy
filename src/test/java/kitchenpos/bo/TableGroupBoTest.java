package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.mock.OrderTableBuilder;
import kitchenpos.mock.TableGroupBuilder;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
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

    @DisplayName("새로운 테이블그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        Long newTableGroupId = 1L;
        OrderTable orderTable1 = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(1)
                .withEmpty(true)
                .build();
        OrderTable orderTable2 = OrderTableBuilder.mock()
                .withId(2L)
                .withNumberOfGuests(2)
                .withEmpty(true)
                .build();
        TableGroup newTableGroup = TableGroupBuilder.mock()
                .withOrderTables(new ArrayList<>(Arrays.asList(orderTable1, orderTable2)))
                .build();

        given(orderTableDao.findAllByIdIn(new ArrayList<Long>(Arrays.asList(orderTable1.getId(), orderTable2.getId()))))
                .willReturn(new ArrayList<>(Arrays.asList(orderTable1, orderTable2)));
        given(tableGroupDao.save(newTableGroup)).willAnswer(invocation -> {
            newTableGroup.setId(newTableGroupId);
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
        assertThat(result.getId()).isEqualTo(newTableGroupId);
        assertThat(result.getCreatedDate()).isNotNull();
        assertThat(result.getOrderTables()).containsExactlyInAnyOrder(orderTable1, orderTable2);

        ArgumentCaptor<OrderTable> argument = ArgumentCaptor.forClass(OrderTable.class);
        verify(orderTableDao, times(2)).save(argument.capture());
        assertThat(argument.getValue().getTableGroupId()).isEqualTo(newTableGroupId);
        assertThat(argument.getValue().isEmpty()).isEqualTo(false); // 지정한 테이블의 공석여부를 아님으로 설정한다.
    }

    @DisplayName("새로운 테이블그룹을 생성 시, 테이블그룹 내 테이블은 2개 이상이다.")
    @ParameterizedTest
    @MethodSource(value = "provideLessOrderTables")
    void createOnlyWhenTablesAreAtLeast2(List<OrderTable> orderTables) {
        // given
        TableGroup newTableGroup = TableGroupBuilder.mock()
                .withOrderTables(orderTables)
                .build();

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
        TableGroup newTableGroup = TableGroupBuilder.mock()
                .withOrderTables(orderTables)
                .build();

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
        OrderTableBuilder orderTableBuilder = OrderTableBuilder.mock()
                .withNumberOfGuests(1);

        return Stream.of(
                new ArrayList<>(Arrays.asList(
                        orderTableBuilder.withId(1L).withEmpty(true).build(),
                        orderTableBuilder.withId(2L).withEmpty(false).build()
                )),
                new ArrayList<>(Arrays.asList(
                        orderTableBuilder.withId(1L).withEmpty(false).build(),
                        orderTableBuilder.withId(2L).withEmpty(false).build()
                ))
        );
    }

    @DisplayName("테이블그룹 생성 시, 지정한 테이블이 다른 테이블그룹에 포함되어있지 않아야한다.")
    @ParameterizedTest
    @MethodSource(value = "provideNotFreeOrderTables")
    void createOnlyWhenTablesAreNotIncludedInTableGroup(List<OrderTable> orderTables) {
        // given
        TableGroup newTableGroup = TableGroupBuilder.mock()
                .withOrderTables(orderTables)
                .build();

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
        OrderTableBuilder orderTableBuilder = OrderTableBuilder.mock()
                .withNumberOfGuests(1)
                .withEmpty(true);

        return Stream.of(
                new ArrayList<>(Arrays.asList(
                        orderTableBuilder.withId(1L).build(),
                        orderTableBuilder.withId(1L).withTableGroupId(1L).build()
                )),
                new ArrayList<>(Arrays.asList(
                        orderTableBuilder.withId(1L).withTableGroupId(1L).build(),
                        orderTableBuilder.withId(2L).withTableGroupId(2L).build()
                ))
        );
    }

    @DisplayName("테이블그룹을 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        Long tableGroupId = 1L;
        OrderTable orderTable1 = OrderTableBuilder.mock()
                .withId(1L)
                .withTableGroupId(tableGroupId)
                .withNumberOfGuests(1)
                .withEmpty(true)
                .build();
        OrderTable orderTable2 = OrderTableBuilder.mock()
                .withId(2L)
                .withTableGroupId(tableGroupId)
                .withNumberOfGuests(2)
                .withEmpty(true)
                .build();
        List<OrderTable> orderTables = new ArrayList<>(Arrays.asList(orderTable1, orderTable2));

        given(orderTableDao.findAllByTableGroupId(tableGroupId)).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class))).willReturn(null);

        // when
        tableGroupBo.delete(tableGroupId);

        // then
        ArgumentCaptor<OrderTable> argument = ArgumentCaptor.forClass(OrderTable.class);
        verify(orderTableDao, times(orderTables.size())).save(argument.capture());
        assertThat(argument.getValue().getTableGroupId()).isNull(); // 테이블그룹에 지정되었던 테이블들을 지정 해제한다.
    }

    @DisplayName("테이블그룹 삭제 시, 테이블그룹에 포함된 테이블들에서 발생한 모든 주문들의 주문상태가 완료인 경우에만 삭제할 수 있다.")
    @Test
    void deleteOnlyWhenOrdersAreCompleted() {
        // given
        Long tableGroupId = 1L;
        OrderTable orderTable1 = OrderTableBuilder.mock()
                .withId(1L)
                .withTableGroupId(tableGroupId)
                .withNumberOfGuests(1)
                .withEmpty(true)
                .build();
        OrderTable orderTable2 = OrderTableBuilder.mock()
                .withId(2L)
                .withTableGroupId(tableGroupId)
                .withNumberOfGuests(2)
                .withEmpty(true)
                .build();
        List<OrderTable> orderTables = new ArrayList<>(Arrays.asList(orderTable1, orderTable2));

        given(orderTableDao.findAllByTableGroupId(1L)).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(orderTables.stream()
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
