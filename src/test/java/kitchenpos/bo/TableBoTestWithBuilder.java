package kitchenpos.bo;

import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

public class TableBoTestWithBuilder extends MockTest{
    @Mock
    private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @InjectMocks
    private TableBo tableBo;

    @DisplayName("테이블 정보를 입력할 수 있다. (테이블그룹, 손님 수, 테이블상태(비었는지 아닌지))")
    @Test
    void create() {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withId(1L)
                .build();
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(givenOrderTable);

        //when
        OrderTable actualOrderTable = tableBo.create(givenOrderTable);

        //then
        assertThat(actualOrderTable.getId()).isEqualTo(givenOrderTable.getId());
    }

    @DisplayName("테이블 목록을 볼 수 있다.")
    @Test
    void list() {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .build();
        List<OrderTable> givenOrderTables = Arrays.asList(givenOrderTable);
        given(orderTableDao.findAll())
                .willReturn(givenOrderTables);

        //when
        List<OrderTable> actualOrderTableList = tableBo.list();

        //then
        assertThat(actualOrderTableList.size())
                .isEqualTo(givenOrderTables.size());
    }

    @DisplayName("테이블상태를 변경할 수 있다.")
    @Test
    void changeEmpty() {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withId(1L)
                .build();
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any(List.class)))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(givenOrderTable);

        //when
        OrderTable actualOrderTable =
                tableBo.changeEmpty(givenOrderTable.getId(), givenOrderTable);

        //then
        assertThat(actualOrderTable.isEmpty())
                .isFalse();
    }

    @DisplayName("주문이 없는 테이블의 상태를 변경할 수 없다.")
    @Test
    void changeEmptyWithoutOrderTable() {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withId(1L)
                .build();
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(givenOrderTable.getId(), givenOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹에 포함되지 않은 테이블은 변경할 수 없다.")
    @ParameterizedTest
    @NullSource
    void changeEmptyExclusiveTableGroup(Long tableGroupId) {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withTableGroupId(tableGroupId)
                .withId(1L)
                .build();

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(givenOrderTable.getId(), givenOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("조리중이거나 손님이 식사중인 테이블의 상태를 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true})
    void changeEmptyCookingOrMeal(boolean isEmpty) {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withTableGroupId(null)
                .withId(1L)
                .build();
        given(orderTableDao.findById(givenOrderTable.getId()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(givenOrderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(isEmpty);

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(givenOrderTable.getId(), givenOrderTable); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블에 손님수를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    void changeNumberOfGuests(int numberOfGuests) {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withNumberOfGuests(numberOfGuests)
                .withId(1L)
                .build();
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(givenOrderTable);

        //when
        OrderTable actualOrderTable =
                tableBo.changeNumberOfGuests(givenOrderTable.getId(), givenOrderTable);

        //then
        assertThat(actualOrderTable.getNumberOfGuests())
                .isEqualTo(givenOrderTable.getNumberOfGuests());
    }

    @DisplayName("손님수는 반드시 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2})
    void changeNumberOfGuestsByNegativeNumber(int numberOfGuests) {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withNumberOfGuests(numberOfGuests)
                .withId(1L)
                .build();

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(givenOrderTable.getId(), givenOrderTable); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 테이블의 손님수만 변경할 수 있다.")
    @Test
    void changeNumberOfGuestsTableWithOrder() {
        //given
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .withId(1L)
                .build();
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(givenOrderTable.getId(), givenOrderTable); })
                .isInstanceOf(IllegalArgumentException.class);
    }
}
