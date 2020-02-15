package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import kitchenpos.model.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

class TableGroupBoTest extends MockTest {
    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("테이블 그룹을 등록할 수 있다")
    @Test
    void createTableGroup() {
        OrderTable orderTable1 = TestFixtures.customOrderTable(null, true);
        OrderTable orderTable2 = TestFixtures.customOrderTable(null, true);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup expected = TestFixtures.tableGroup(orderTables);

        //given
        given(orderTableDao.findAllByIdIn(expected.getOrderTables().stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()))
        ).willReturn(Arrays.asList(orderTable1, orderTable2));
        given(tableGroupDao.save(expected)).willReturn(expected);

        //when
        TableGroup result = tableGroupBo.create(expected);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("올바르지 않은 테이블을 테이블 그룹에 등록할 수 없다")
    @ParameterizedTest
    @MethodSource("invalidTable")
    void createInvalidTable(List<OrderTable> orderTables) {
        TableGroup expected = TestFixtures.tableGroup(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(expected));
    }

    static Stream<List<OrderTable>> invalidTable() {
        OrderTable orderTable1 = TestFixtures.customOrderTable(null, true);
        return Stream.of(new ArrayList<>(), Arrays.asList(orderTable1));
    }

    @DisplayName("테이블 그룹을 삭제할 수 있다")
    @Test
    void deleteTableGroup() {
        OrderTable orderTable1 = TestFixtures.customOrderTable(null, true);
        OrderTable orderTable2 = TestFixtures.customOrderTable(null, true);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        TableGroup expected = TestFixtures.tableGroup(orderTables);

        //given
        given(orderTableDao.findAllByTableGroupId(expected.getId())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(expected.getOrderTables().stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()), orderStatuses)
        ).willReturn(false);
        given(orderTableDao.save(orderTables.get(0))).willReturn(orderTables.get(0));
        given(orderTableDao.save(orderTables.get(1))).willReturn(orderTables.get(1));

        //when
        tableGroupBo.delete(expected.getId());
    }

    @DisplayName("완료되지 않은 테이블 그룹은 삭제할 수 없다")
    @Test
    void deleteTableGroupNotFinished() {
        OrderTable orderTable1 = TestFixtures.customOrderTable(null, true);
        OrderTable orderTable2 = TestFixtures.customOrderTable(null, true);
        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        TableGroup expected = TestFixtures.tableGroup(orderTables);

        //given
        given(orderTableDao.findAllByTableGroupId(expected.getId())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(expected.getOrderTables().stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList()), orderStatuses)
        ).willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(expected.getId()));
    }
}
