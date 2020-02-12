package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.mock.OrderTableBuilder;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("새로운 테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable newOrderTable = OrderTableBuilder.mock()
                .withTableGroupId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        given(orderTableDao.save(newOrderTable)).willAnswer(invocation -> {
            newOrderTable.setId(1L);
            return newOrderTable;
        });

        // when
        OrderTable result = tableBo.create(newOrderTable);

        // then
        assertThat(result.getId()).isEqualTo(newOrderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(newOrderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(newOrderTable.getNumberOfGuests());
        assertThat(result.isEmpty()).isEqualTo(newOrderTable.isEmpty());
    }

    @DisplayName("테이블 정보를 수정할 수 있다. (테이블그룹, 게스트 수, 공석여부)")
    @Test
    void update() {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withTableGroupId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        OrderTable result = tableBo.create(orderTable);

        // then
        assertThat(result.getId()).isEqualTo(orderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(orderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        assertThat(result.isEmpty()).isEqualTo(orderTable.isEmpty());
    }

    @DisplayName("전체 테이블 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withTableGroupId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        given(orderTableDao.findAll()).willReturn(Collections.singletonList(orderTable));

        // when
        List<OrderTable> result = tableBo.list();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).containsExactlyInAnyOrder(orderTable);
    }

    @DisplayName("테이블의 공석여부를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean empty) {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        OrderTable parameter = OrderTableBuilder.mock()
                .withEmpty(empty)
                .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(orderTable)).willAnswer(invocation -> {
            orderTable.setEmpty(parameter.isEmpty());
            return orderTable;
        });

        // when
        OrderTable result = tableBo.changeEmpty(orderTable.getId(), parameter);

        // then
        assertThat(result.getId()).isEqualTo(orderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(orderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        assertThat(result.isEmpty()).isEqualTo(empty);
    }

    @DisplayName("테이블 공석여부 변경 시, 해당 테이블이 테이블그룹에 포함된 경우 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cannotChangeEmptyWhenIncludedByTableGroup(boolean empty) {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withTableGroupId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        OrderTable parameter = OrderTableBuilder.mock()
                .withEmpty(empty)
                .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(orderTable.getId(), parameter);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 공석여부 변경 시, 테이블에서 발생한 주문들의 주문상태가 완료인 경우에만 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cannotChangeEmptyWhenOrdersAreNotCompleted(boolean empty) {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        OrderTable parameter = OrderTableBuilder.mock()
                .withEmpty(empty)
                .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(orderTable.getId(), parameter);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 게스트 수를 수정할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 200})
    void changeNumberOfGuests(int numberOfGuests) {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(5)
                .withEmpty(false)
                .build();

        OrderTable parameter = OrderTableBuilder.mock()
                .withNumberOfGuests(numberOfGuests)
                .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable)).willAnswer(invocation -> {
            orderTable.setNumberOfGuests(numberOfGuests);
            return orderTable;
        });

        // when
        OrderTable result = tableBo.changeNumberOfGuests(orderTable.getId(), parameter);

        // then
        assertThat(result.getId()).isEqualTo(orderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(orderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(numberOfGuests);
        assertThat(result.isEmpty()).isEqualTo(orderTable.isEmpty());
    }

    @DisplayName("테이블의 게스트 수 수정 시, 게스트 수는 0명 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-1000, -100, -10, -1})
    void changeNumberOfGuestsOnlyWhenEqualsOrLargerThan0(int numberOfGuests) {
        // given
        OrderTable parameter = OrderTableBuilder.mock()
                .withNumberOfGuests(numberOfGuests)
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(1L, parameter);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 게스트 수 수정 시, 테이블이 공석 상태라면 수정할 수 없다.")
    @Test
    void changeNumberOfGuestsOnlyWhenNotEmpty() {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(5)
                .withEmpty(true)
                .build();

        OrderTable parameter = OrderTableBuilder.mock()
                .withNumberOfGuests(5)
                .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTable.getId(), parameter);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
