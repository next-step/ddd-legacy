package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable orderTable;
    private List<String> orderStatusList;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(false);
        orderStatusList = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
    }

    @DisplayName("테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        final OrderTable actual = tableBo.create(orderTable);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("테이블을 검색할 수 있다.")
    @Test
    void list() {
        // given
        given(orderTableDao.findAll())
                .willReturn(Collections.singletonList(orderTable));

        // when
        List<OrderTable> orderTableList = tableBo.list();
        OrderTable actual = orderTableList.get(0);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("테이블의 상태를 변경 할 수 있다")
    @Test
    void changeEmpty() {
        // given
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), orderStatusList))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        OrderTable actual = tableBo.changeEmpty(orderTable.getId(), orderTable);

        // then
        assertThat(actual.isEmpty()).isEqualTo(orderTable.isEmpty());
    }

    @DisplayName("테이블을 빈테이블로 변경하려면 주문상태가 완료여야한다.")
    @Test
    void changeTableShouldCompleteStatus() {
        // given
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), orderStatusList))
                .willReturn(true);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("테이블을 빈테이블로 변경하려면 테이블이 존재해야한다.")
    @Test
    void changeTableShouldExist() {
        given(orderTableDao.findById(anyLong()))
                .willThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(),orderTable));
    }

    @DisplayName("테이블이 테이블그룹에 속하면 빈테이블로 변경할 수 없다.")
    @Test
    void tableShouldNotBelongToOtherTableGroup() {
        orderTable.setTableGroupId(1L);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("손님수를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    void changeNumberOfGuests(int guest) {
        // given
        orderTable.setNumberOfGuests(guest);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(orderTable);

        // when
        OrderTable actual = tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("손님수를 변경하기 위해서 테이블에 손님이 있어야한다.")
    @ParameterizedTest
    @ValueSource(ints = {0})
    void guestShouldExistChangeTable(int guest) {
        // given
        orderTable.setNumberOfGuests(guest);

        // when
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(orderTable);
        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("빈테이블 손님수는 변경할 수 없다.")
    @Test
    void changeTableShouldNotEmpty() {
        // given
        given(orderTableDao.findById(anyLong()))
                .willThrow(IllegalArgumentException.class);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(),orderTable));
    }
}
