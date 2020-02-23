package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
@ExtendWith(MockitoExtension.class)
class TableBoTest extends Fixtures {

    @InjectMocks
    private TableBo tableBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Test
    @DisplayName("주문 테이블을 등록할 수 있다.")
    void create() {
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        final OrderTable savedOrderTable = tableBo.create(orderTable);

        assertThat(savedOrderTable).isNotNull();
        assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @Test
    @DisplayName("주문 테이블 목록을 조회 할 수 있다.")
    void list() {
        final List<OrderTable> orderTables = Arrays.asList(orderTable);

        given(orderTableDao.findAll()).willReturn(orderTables);

        final List<OrderTable> list = tableBo.list();

        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(orderTables.size());
    }

    @Test
    @DisplayName("주문 테이블의 상태를 비움으로 업데이트 할 수 있다.")
    void changeEmpty() {
        final OrderTable newOrderTable = orderTable;
        newOrderTable.setTableGroupId(null);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(orderTable)).willReturn(newOrderTable);

        final OrderTable result = tableBo.changeEmpty(orderTable.getId(), newOrderTable);

        assertThat(result.isEmpty()).isEqualTo(newOrderTable.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    @DisplayName("주문 테이블이 정상 값이 아닌 경우")
    void changeEmpty_exist_tableGroupId(final Long orderTableId) {
        orderTable.setId(orderTableId);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable));
    }

    @Test
    @DisplayName("반드시 식사가 끝난 경우에 상태를 비움으로 업데이트 할 수 있다.")
    void changeEmpty_check_orderStatus() {
        final OrderTable resolvedOrderTable = orderTable;
        resolvedOrderTable.setTableGroupId(null);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(resolvedOrderTable.getId(),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @Test
    @DisplayName("특정 테이블의 인원수를 업데이트 한다.")
    void changeNumberOfGuests() {
        final OrderTable newOrderTable = orderTable;
        newOrderTable.setNumberOfGuests(4);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        final OrderTable result = tableBo.changeNumberOfGuests(orderTable.getId(), newOrderTable);

        assertThat(result.getNumberOfGuests()).isEqualTo(newOrderTable.getNumberOfGuests());
    }
}
