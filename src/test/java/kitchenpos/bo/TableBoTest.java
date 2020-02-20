package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TestOrderDao;
import kitchenpos.dao.TestOrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TableBoTest {

    private OrderDao orderDao = new TestOrderDao();
    private OrderTableDao orderTableDao = new TestOrderTableDao();

    private TableBo tableBo;

    private Random random = new Random();

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @DisplayName("테이블을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final OrderTable expected = createOrderTable();

        // when
        final OrderTable actual = tableBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.isEmpty()).isEqualTo(expected.isEmpty());
        assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @DisplayName("테이블 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final OrderTable expected = createOrderTable();
        orderTableDao.save(expected);

        // when
        final List<OrderTable> actual = tableBo.list();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).contains(expected);
    }

    @DisplayName("테이블을 비어있는 상태로 변경할 수 있다.")
    @Test
    void changeEmpty() {
        // given
        final Long orderTableId = 1L;
        orderTableDao.save(createOrderTable(orderTableId, 3));

        final OrderTable expected = createOrderTable(orderTableId, 0);

        // when
        final OrderTable actual = tableBo.changeEmpty(orderTableId, expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.isEmpty()).isEqualTo(true);
    }

    @DisplayName("식사가 모두 완료되지 않으면 테이블을 비어있는 상태로 변경할 수 없다.")
    @Test
    void notYetFinished() {
        // given
        final Long orderTableId = 1L;
        orderTableDao.save(createOrderTable(orderTableId, 3));

        final Order notYetFinishedOrder = new Order() {{
            setId(random.nextLong());
            setOrderTableId(orderTableId);
            setOrderStatus(OrderStatus.COOKING.name());
        }};
        orderDao.save(notYetFinishedOrder);

        final OrderTable expected = createOrderTable(orderTableId, 0);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTableId, expected));
    }

    @DisplayName("테이블 그룹에 속해있는 테이블은 비어있는 상태로 변경할 수 없다.")
    @Test
    void inGroup() {
        // given
        final Long orderTableId = 1L;
        final Long tableGroupId = 1L;
        orderTableDao.save(createOrderTableInGroup(orderTableId, tableGroupId, 3));

        final OrderTable expected = createOrderTableInGroup(orderTableId, tableGroupId, 0);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTableId, expected));
    }

    @DisplayName("테이블의 인원을 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void changeNumberOfGuests(final int numberOfGuests) {
        // given
        final Long orderTableId = 1L;
        orderTableDao.save(createOrderTable(orderTableId, 3));

        final OrderTable expected = createOrderTable(orderTableId, numberOfGuests);

        // when
        final OrderTable actual = tableBo.changeNumberOfGuests(orderTableId, expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(actual.getNumberOfGuests());
        assertThat(actual.isEmpty()).isEqualTo(actual.isEmpty());
    }

    @DisplayName("인원이 올바르지 않으면 테이블의 인원을 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1})
    void invalidNumberOfGuests(final int numberOfGuests) {
        // given
        final OrderTable expected = createOrderTable(numberOfGuests);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(expected.getId(), expected));
    }

    private OrderTable createOrderTable() {
        return createOrderTable(random.nextLong(), 3);
    }

    private OrderTable createOrderTable(int numberOfGuests) {
        return createOrderTable(random.nextLong(), numberOfGuests);
    }

    private OrderTable createOrderTable(Long id, int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(numberOfGuests <= 0);

        return orderTable;
    }

    private OrderTable createOrderTableInGroup(Long id, Long tableGroupId, int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(numberOfGuests <= 0);

        return orderTable;
    }
}
