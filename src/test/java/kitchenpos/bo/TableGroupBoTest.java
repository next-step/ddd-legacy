package kitchenpos.bo;

import kitchenpos.TestFixtures;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private TableGroup tableGroupExpected;

    @Test
    @DisplayName("두 자리 미만의 테이블을 사용할 수 없다.")
    void createExceptionByTableSize() {
        // give
        tableGroupExpected = TestFixtures.createTableGroupByTableOne();
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.create(tableGroupExpected));
    }

    @Test
    @DisplayName("주문하려는 자리들과 식당에 존재하는 자리의 수가 다를 때 주문을 할 수 없다.")
    void createExceptionByTables() {
        // give
        List<OrderTable> orderTables = TestFixtures.createOrderTablesByLoopIndex(3L);
        List<OrderTable> orderTablesExpected = TestFixtures.createOrderTablesByLoopIndex(2L);
        tableGroupExpected = TestFixtures.createTableGroupSizeTwo();
        tableGroupExpected.setOrderTables(orderTablesExpected);

        given(orderTableDao.findAllByIdIn(Arrays.asList(1L, 2L)))
                .willReturn(orderTables);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.create(tableGroupExpected));
    }

    @Test
    @DisplayName("이용하려는 자리들이 비어있지 않다면 자리에 앉을 수 없다.")
    void createExceptionByNotEmptyTable() {
        // give
        List<OrderTable> orderTablesExpected = TestFixtures.createOrderTablesByLoopIndex(2L);
        tableGroupExpected = TestFixtures.createTableGroupSizeTwo();

        given(orderTableDao.findAllByIdIn(Arrays.asList(1L, 2L)))
                .willReturn(orderTablesExpected);

        tableGroupExpected.setOrderTables(orderTablesExpected);

        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.create(tableGroupExpected));
    }

    @Test
    @DisplayName("주문 상태가 요리 중, 먹는 중일 때 주문 테이블을 정리 할 수 없다.")
    void deleteExceptionByStatus() {
        // give
        List<OrderTable> orderTablesExpected = TestFixtures.createOrderTablesByLoopIndex(2L);

        given(orderTableDao.findAllByTableGroupId(1L))
                .willReturn(orderTablesExpected);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                Arrays.asList(1L, 2L),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.delete(1L));
    }
}
