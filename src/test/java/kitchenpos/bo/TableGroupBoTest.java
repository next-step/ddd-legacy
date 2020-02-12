package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import kitchenpos.support.OrderTableBuilder;
import kitchenpos.support.TableGroupBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TableGroupBoTest extends MockTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo sut;

    @Test
    @DisplayName("단체 그룹 생성")
    void createTableGroup() {
        // given
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable();
        final List<OrderTable> orderTables = Arrays.asList(
                orderTableBuilder.withId(1).withEmpty(true).build(),
                orderTableBuilder.withId(2).withEmpty(true).build()
        );
        final TableGroupBuilder tableGroupBuilder = TableGroupBuilder.tableGroup()
                .withOrderTables(orderTables);

        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(orderTables);
        given(tableGroupDao.save(any(TableGroup.class)))
                .willReturn(tableGroupBuilder.withId(11).build());

        // when
        TableGroup saved = sut.create(tableGroupBuilder.build());

        // then
        for (int i = 0; i < orderTables.size(); i++) {
            assertThat(saved.getOrderTables().get(i)).isEqualToComparingFieldByField(orderTables.get(i));
        }

        // and
        verify(orderTableDao).findAllByIdIn(anyList());
        verify(tableGroupDao).save(any(TableGroup.class));
        verifyNoInteractions(orderDao);
    }


    @ParameterizedTest
    @MethodSource("provideCheckOrderTablesNo")
    @DisplayName("단체 그룹 생성시 테이블이 2개 미만이면 예외 던짐")
    void shouldThrowExceptionWhenTheNumberOfOrderTablesIsUnderTwo(List<OrderTable> orderTables) {
        // given
        final TableGroupBuilder tableGroupBuilder = TableGroupBuilder.tableGroup()
                .withOrderTables(orderTables);

        // when & then
        assertThatThrownBy(() -> sut.create(tableGroupBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verifyNoInteractions(orderTableDao);
        verifyNoInteractions(tableGroupDao);
        verifyNoInteractions(orderDao);
    }

    private static Stream provideCheckOrderTablesNo() {
        return Stream.of(
                Arguments.of(Collections.EMPTY_LIST),
                Arguments.of(Collections.singletonList(
                        OrderTableBuilder.orderTable().withId(1).withEmpty(true).build()
                ))
        );
    }

    @Test
    @DisplayName("단체 그룹 생성시 요청한 테이블 정보와 조회한 테이블의 수가 일치하지 않으면 예외 던짐")
    void shouldThrowExceptionWhenNotMatchOrderTables() {
        // given
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable();
        final List<OrderTable> requestTables = Arrays.asList(
                orderTableBuilder.withId(1).withEmpty(true).build(),
                orderTableBuilder.withId(2).withEmpty(true).build()
        );
        final List<OrderTable> savedTables = Collections.singletonList(
                orderTableBuilder.withId(1).withEmpty(true).build()
        );
        final TableGroupBuilder tableGroupBuilder = TableGroupBuilder.tableGroup()
                .withOrderTables(requestTables);

        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(savedTables);

        // when & then
        assertThatThrownBy(() -> sut.create(tableGroupBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verify(orderTableDao).findAllByIdIn(anyList());
        verifyNoInteractions(tableGroupDao);
        verifyNoInteractions(orderDao);
    }

    @ParameterizedTest
    @MethodSource("provideOrderTables")
    @DisplayName("단체 그룹 생성시 테이블이 비어있지 않거나 단체 그룹으로 묶여 있으면 예외 던짐")
    void shouldThrowExceptionWhen(List<OrderTable> orderTables) {
        // given
        final TableGroupBuilder tableGroupBuilder = TableGroupBuilder.tableGroup()
                .withOrderTables(orderTables);

        given(orderTableDao.findAllByIdIn(anyList()))
                .willReturn(orderTables);

        // when & then
        assertThatThrownBy(() -> sut.create(tableGroupBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verify(orderTableDao).findAllByIdIn(anyList());
        verifyNoInteractions(tableGroupDao);
        verifyNoInteractions(orderDao);
    }

    private static Stream provideOrderTables() {
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable()
                .withId(1)
                .withNoOfGuests(1)
                .withEmpty(true);

        return Stream.of(
                Arguments.of(Arrays.asList(
                    orderTableBuilder.withEmpty(false).build(),
                    orderTableBuilder.build()
                )),
                Arguments.of(Arrays.asList(
                    orderTableBuilder.withtTableGroupId(11).build(),
                    orderTableBuilder.build()
                ))
        );
    }

    @Test
    @DisplayName("단체 그룹 삭제")
    void deleteTableGroup() {
        // given
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable();
        final List<OrderTable> orderTables = Arrays.asList(
                orderTableBuilder.withId(1).withEmpty(true).build(),
                orderTableBuilder.withId(2).withEmpty(true).build()
        );

        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(OrderTableBuilder.EMPTY_ORDER_TABLE)
                .willReturn(OrderTableBuilder.EMPTY_ORDER_TABLE);

        // when
        sut.delete(1L);

        // then
        verify(orderTableDao).findAllByTableGroupId(anyLong());
        verify(orderDao).existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList());
        verify(orderTableDao, times(orderTables.size())).save(any(OrderTable.class));
    }

    @Test
    @DisplayName("단체 그룹 삭제시 요리중이거나 식사중이면 예외 발생")
    void shouldThrowExceptionWithNotCompletedOrderStatus() {
        // given
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable();
        final List<OrderTable> orderTables = Arrays.asList(
                orderTableBuilder.withId(1).withEmpty(true).build(),
                orderTableBuilder.withId(2).withEmpty(true).build()
        );

        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList()))
                .willReturn(true);

        // when
        assertThatThrownBy(() -> sut.delete(1L))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(orderTableDao).findAllByTableGroupId(anyLong());
        verify(orderDao).existsByOrderTableIdInAndOrderStatusIn(anyList(), anyList());
    }
}
