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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTests {
    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable mockOrderTable = new OrderTable();
    private final List<OrderTable> mockOrderTables = new ArrayList<>();

    @BeforeEach
    public void setup() {
        // { id: 1, empty: false, tableGroupId: 2, numberOfGuests: 3 }
        setupOrderTable();

        mockOrderTables.add(mockOrderTable);
    }

    @DisplayName("주문 테이블 생성 시도 성공")
    @Test
    public void createOrderTableSuccess() {
        given(orderTableDao.save(mockOrderTable)).willReturn(mockOrderTable);

        OrderTable orderTable = tableBo.create(mockOrderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(3);
    }

    @DisplayName("주문 테이블 전체 조회 성공")
    @Test
    public void getAllOrderTable() {
        given(orderTableDao.findAll()).willReturn(mockOrderTables);

        List<OrderTable> orderTables = tableBo.list();

        assertThat(orderTables).hasSize(1);
    }

    @DisplayName("테이블 그룹에 속하지 않고 식사 상태가 완료인 주문 테이블의 공석 여부 변경 시도 성공")
    @Test
    public void changeOrderTableEmptySuccess() {
        OrderTable emptyTable = new OrderTable();
        emptyTable.setEmpty(true);

        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(mockOrderTable));
        given(orderTableDao.save(mockOrderTable)).willReturn(mockOrderTable);

        OrderTable orderTable = tableBo.changeEmpty(1L, emptyTable);

        assertThat(orderTable.isEmpty()).isEqualTo(true);
    }

    @DisplayName("존재하지 않는 주문 테이블의 공석 여부 변경 시도 시 실패")
    @Test
    public void changeOrderTableEmptyFailWithNotExistOrderTable() {
        assertThatThrownBy(() -> tableBo.changeEmpty(1L, new OrderTable()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 그룹에 속한 주문 테이블의 공석 여부 변경 시도 시 실패")
    @Test
    public void changeOrderTableEmptyFailWhenInTableGroup() {
        mockOrderTable.setTableGroupId(33L);
        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(mockOrderTable));

        assertThatThrownBy(() -> tableBo.changeEmpty(1L, new OrderTable()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("조리중이거나 식사중인 주문 테이블의 공석 여부 변경 시도 시 실패")
    @Test
    public void changeOrderTableEmptyFailWhenInCookingOrMeal() {
        List<String> statuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(mockOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(1L, statuses)).willReturn(true);

        assertThatThrownBy(() -> tableBo.changeEmpty(1L, new OrderTable()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비어있지 않은 주문 테이블의 손님 수 변경 시도 성공")
    @Test
    public void changeOrderTablePeopleSuccess() {
        OrderTable sevenPeopleTable = new OrderTable();
        sevenPeopleTable.setNumberOfGuests(7);

        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(mockOrderTable));
        given(orderTableDao.save(mockOrderTable)).willReturn(mockOrderTable);

        OrderTable orderTable = tableBo.changeNumberOfGuests(1L, sevenPeopleTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(7);
    }

    @DisplayName("존재하지 않는 주문 테이블의 손님 수 변경 시도 시 실패")
    @Test
    public void changeOrderTablePeopleFailWithNotExistOrderTable() {
        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(1L, new OrderTable()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블의 손님 수를 음수로 변경 시도 시 실패")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2})
    public void changeOrderTablePeopleFailWhenTryToUnderZero(int numberOfPeople) {
        OrderTable invalidTable = new OrderTable();
        invalidTable.setNumberOfGuests(numberOfPeople);

        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(1L, invalidTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비어있는 주문 테이블의 손님 수 변경 시도 시 실패")
    @Test
    public void changeOrderTablePeopleFailToEmptyOrderTable() {
        mockOrderTable.setEmpty(true);
        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(mockOrderTable));

        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(1L, new OrderTable()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void setupOrderTable() {
        mockOrderTable.setId(1L);
        mockOrderTable.setEmpty(false);
        mockOrderTable.setNumberOfGuests(3);
    }
}
