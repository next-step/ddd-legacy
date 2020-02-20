package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Order;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TableGroupBoTest {

    private OrderDao orderDao = new TestOrderDao();
    private OrderTableDao orderTableDao = new TestOrderTableDao();
    private TableGroupDao tableGroupDao = new TestTableGroupDao();

    private TableGroupBo tableGroupBo;

    private Random random = new Random();

    @BeforeEach
    void setUp() {
        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);

        final OrderTable orderTable1 = createEmptyOrderTable(1L);
        orderTableDao.save(orderTable1);

        final OrderTable orderTable2 = createEmptyOrderTable(2L);
        orderTableDao.save(orderTable2);

        final Long tableGroupId = 1L;
        final TableGroup expected = new TableGroup() {{
            setId(tableGroupId);
        }};
        tableGroupDao.save(expected);

        final OrderTable orderTable3 = createEmptyOrderTableInGroup(3L, tableGroupId);
        orderTableDao.save(orderTable3);

        final OrderTable orderTable4 = createEmptyOrderTableInGroup(4L, tableGroupId);
        orderTableDao.save(orderTable4);
    }

    @DisplayName("테이블 그룹을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final TableGroup expected = new TableGroup();
        expected.setId(random.nextLong());
        expected.setOrderTables(Arrays.asList(
                orderTableDao.findById(1L).orElseThrow(IllegalArgumentException::new),
                orderTableDao.findById(2L).orElseThrow(IllegalArgumentException::new))
        );

        // when
        final TableGroup actual = tableGroupBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getOrderTables().size()).isEqualTo(expected.getOrderTables().size());
    }

    @DisplayName("2개 미만의 테이블이 포함되는 그룹은 등록할 수 없다.")
    @Test
    void tooLessOrderTable() {
        // given
        final TableGroup expected = new TableGroup();
        expected.setId(random.nextLong());
        expected.setOrderTables(Arrays.asList(
                orderTableDao.findById(1L).orElseThrow(IllegalArgumentException::new))
        );

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(expected));
    }

    @DisplayName("테이블 그룹을 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        final TableGroup existingTableGroup = tableGroupDao.findAll().get(0);

        // when
        tableGroupBo.delete(existingTableGroup.getId());

        // then
        List<OrderTable> actual = orderTableDao.findAllByTableGroupId(existingTableGroup.getId());
        assertThat(actual).isEmpty();
    }

    @DisplayName("모든 테이블의 식사가 완료되지 않으면 테이블 그룹을 삭제할 수 없다.")
    @Test
    void notYetFinished() {
        // given
        final TableGroup existingTableGroup = tableGroupDao.findAll().get(0);
        final OrderTable existingOrderTableInGroup = orderTableDao.findAllByTableGroupId(existingTableGroup.getId()).get(0);

        final Order order = new Order() {{
            setId(random.nextLong());
            setOrderTableId(existingOrderTableInGroup.getId());
            setOrderStatus(OrderStatus.MEAL.name());
        }};
        orderDao.save(order);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(existingTableGroup.getId()));
    }

    private OrderTable createEmptyOrderTable(Long id) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);

        return orderTable;
    }

    private OrderTable createEmptyOrderTableInGroup(Long id, Long tableGroupId) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);

        return orderTable;
    }
}
