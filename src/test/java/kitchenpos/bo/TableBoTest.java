package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

class TableBoTest extends MockTest {
    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("테이블을 등록할 수 있다")
    @Test
    void createTable() {
        OrderTable expected = TestFixtures.orderTable();

        //given
        given(orderTableDao.save(expected)).willReturn(expected);

        //when
        OrderTable result = tableBo.create(expected);

        //then
        assertAll(
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests()),
                () -> assertThat(result.isEmpty()).isEqualTo(expected.isEmpty())
        );
    }

    @DisplayName("테이블을 조회할 수 있다")
    @Test
    void listTable() {
        List<OrderTable> expected = new ArrayList<>();
        OrderTable orderTable = TestFixtures.orderTable();
        expected.add(orderTable);

        //given
        given(orderTableDao.findAll()).willReturn(expected);

        //when
        List<OrderTable> result = tableBo.list();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("테이블의 상태를 변경 할 수 있다")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean isEmpty) {
        OrderTable expected = TestFixtures.customOrderTable(null, isEmpty);
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        //given
        given(orderTableDao.findById(expected.getId())).willReturn(Optional.of(expected));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(expected.getId(), orderStatuses)).willReturn(false);
        given(orderTableDao.save(expected)).willReturn(expected);

        //when
        OrderTable result = tableBo.changeEmpty(expected.getId(), expected);

        //then
        assertThat(result.isEmpty()).isEqualTo(expected.isEmpty());
    }

    @DisplayName("테이블 그룹에 속한 테이블의 상태를 변경 할 수 없다")
    @Test
    void changeEmptyTableInTableGroup() {
        OrderTable expected = TestFixtures.customOrderTable(1L, false);

        //given
        given(orderTableDao.findById(expected.getId())).willReturn(Optional.of(expected));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(expected.getId(), expected));
    }

    @DisplayName("식사가 끝나지 않은 테이블의 상태를 변경 할 수 없다")
    @Test
    void changeEmptyTableNotFinished() {
        OrderTable expected = TestFixtures.customOrderTable(null, false);
        List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        //given
        given(orderTableDao.findById(expected.getId())).willReturn(Optional.of(expected));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(expected.getId(), orderStatuses)).willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(expected.getId(), expected));
    }

    @DisplayName("테이블의 인원 수를 변경 할 수 있다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void changeNumberOfGuests(int numberOfGuest) {
        OrderTable expected = TestFixtures.customGuestOfNumberTable(numberOfGuest);

        //given
        given(orderTableDao.findById(expected.getId())).willReturn(Optional.of(expected));
        given(orderTableDao.save(expected)).willReturn(expected);

        //when
        OrderTable result = tableBo.changeNumberOfGuests(expected.getId(), expected);

        //then
        assertThat(result.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @DisplayName("테이블의 인원 수를 음수로 변경 할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void changeNegativeNumberOfGuests(int numberOfGuest) {
        OrderTable expected = TestFixtures.customGuestOfNumberTable(numberOfGuest);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(expected.getId(), expected));
    }

    @DisplayName("비어있는 테이블의 인원 수를 변경 할 수 없다")
    @Test
    void changeNumberOfGuestsEmptyTable() {
        OrderTable expected = TestFixtures.customOrderTable(null, true);

        //given
        given(orderTableDao.findById(expected.getId())).willReturn(Optional.of(expected));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(expected.getId(), expected));
    }
}
