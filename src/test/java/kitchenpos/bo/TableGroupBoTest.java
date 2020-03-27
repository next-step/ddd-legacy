package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    public OrderDao orderDao;
    @Mock
    public OrderTableDao orderTableDao;
    @Mock
    public TableGroupDao tableGroupDao;

    @InjectMocks
    TableGroupBo tableGroupBo;

    private TableGroup defaultTableGroup;
    private List<OrderTable> defaultListOrderTables = new ArrayList<>();

    @BeforeEach
    public void setup() {
        defaultTableGroup = Fixtures.getTableGroup(1L, LocalDateTime.now(), defaultListOrderTables);
        defaultListOrderTables.add(Fixtures.getOrderTable(1L, false,3, defaultTableGroup.getId()));
        defaultListOrderTables.add(Fixtures.getOrderTable(2L, false,4, defaultTableGroup.getId()));
    }

    @DisplayName("테이븚 그룹은 2개 이상의 테이블을 가지고 있어야 한다.")
    @Test
    public void createMustHaveLeastTwoTable() {
        if(defaultListOrderTables.size() > 1) {
            defaultListOrderTables.remove(defaultListOrderTables.size() - 1);
        }

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () ->tableGroupBo.create(defaultTableGroup));
    }

    @DisplayName("테이블 그룹을 만들려는 테이블들은 식당에 존재 해야된다.")
    @Test
    public void createSameOrderTableCountSaved() {
        List<Long> defaultOrderTableIds = getIdList(defaultListOrderTables);
        List<OrderTable> savedOrderTables = Lists.newArrayList(defaultListOrderTables);
        if(savedOrderTables.size() > 0) {
            savedOrderTables.remove(savedOrderTables.size() - 1);
        }
        given(orderTableDao.findAllByIdIn(defaultOrderTableIds)).willReturn(savedOrderTables);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> assertThat(tableGroupBo.create(defaultTableGroup)));
    }

    @DisplayName("테이블 그룹을 만들려는 테이블들이 비어 있지 않으면 안된다.")
    @Test
    public void createTablesSavedIsEmpty() {
        List<Long> defaultOrderTableIds = getIdList(defaultListOrderTables);
        List<OrderTable> savedOrderTables = new ArrayList<>();
        savedOrderTables.add(Fixtures.getOrderTable(1L, false, 4));
        savedOrderTables.add(Fixtures.getOrderTable(1L, false, 3));
        given(orderTableDao.findAllByIdIn(defaultOrderTableIds)).willReturn(savedOrderTables);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> assertThat(tableGroupBo.create(defaultTableGroup)));
    }

    @DisplayName("테이블 그룹을 만들려는 테이블들이 이미 테이블 그룹이 되어 있으면 안된다.")
    @Test
    public void createTablesSavedHasNoTableGroup() {
        List<Long> defaultOrderTableIds = getIdList(defaultListOrderTables);
        List<OrderTable> savedOrderTables = new ArrayList<>();
        savedOrderTables.add(Fixtures.getOrderTable(1L, true, 0, 2L));
        savedOrderTables.add(Fixtures.getOrderTable(1L, true, 0, 2L));
        given(orderTableDao.findAllByIdIn(defaultOrderTableIds)).willReturn(savedOrderTables);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> assertThat(tableGroupBo.create(defaultTableGroup)));
    }

    @DisplayName("테이블 그룹에 속한 테이블에 요리중, 식사 상태 인 주문이 있다면 삭제할 수 없다.")
    @Test
    public void deleteNoCookingAndNoMeal() {
        long tableGroupId = defaultTableGroup.getId();
        given(orderTableDao.findAllByTableGroupId(tableGroupId)).willReturn(defaultListOrderTables);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(
                getIdList(defaultListOrderTables), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> tableGroupBo.delete(tableGroupId));
    }

    private List<Long> getIdList(List<OrderTable> orderTables) {
        return orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());
    }
}