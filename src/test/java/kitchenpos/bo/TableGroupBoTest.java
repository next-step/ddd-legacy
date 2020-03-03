package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
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
    }

    @DisplayName("테이븚 그룹은 2개 이상의 테이블을 가지고 있어야 한다.")
    @Test
    public void createMustHaveLeastTwoTable() {
        defaultListOrderTables.add(Fixtures.getOrderTable(1L, false,4, defaultTableGroup.getId()));
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () ->tableGroupBo.create(defaultTableGroup));
    }

    @DisplayName("테이블 그룹을 만들려는 테이블들은 식당에 존재 해야된다.")
    @Test
    public void createSameOrderTableCountSaved() {
        defaultListOrderTables.add(Fixtures.getOrderTable(1L, false,4, defaultTableGroup.getId()));
        defaultListOrderTables.add(Fixtures.getOrderTable(2L, false,3, defaultTableGroup.getId()));

        List<Long> defaultOrderTableIds = defaultListOrderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        List<OrderTable> savedOrderTables = Lists.newArrayList(defaultListOrderTables);
        savedOrderTables.remove(savedOrderTables.size() - 1);

        given(orderTableDao.findAllByIdIn(defaultOrderTableIds)).willReturn(savedOrderTables);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> assertThat(tableGroupBo.create(defaultTableGroup)));
    }

}