package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class TableBoTest {

    @Mock
    OrderTableDao orderTableDao;

    @Mock
    OrderDao orderDao;

    @InjectMocks
    TableBo tableBo;
    private OrderTable orderTable;
    private OrderTable orderTable2;

    @BeforeEach
    void setUp() {
        prepareFixtures();
    }

    private void prepareFixtures() {
        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(0);
        orderTable.setTableGroupId(null);

        orderTable2 = new OrderTable();
        orderTable2.setId(1L);
        orderTable2.setEmpty(false);
        orderTable2.setNumberOfGuests(0);
        orderTable2.setTableGroupId(null);
    }

    @DisplayName("사용자는 새 테이블을 등록할 수 있고, 등록이 완료되면 등록된 테이블 정보를 반환받아 확인할 수 있다")
    @Test
    void create() {
        //given
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        //when
        OrderTable actual = tableBo.create(orderTable);

        //then
        assertThat(actual).isEqualTo(orderTable);
    }

    @DisplayName("사용자는 등록된 테이블의 정보 (테이블 그룹, 착석 여부, 착석 손님 수)를 변경할 수 있다")
    @Test
    void update() {
        //given
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        //when
        OrderTable table = tableBo.create(orderTable);
        table.setTableGroupId(1L);
        table.setEmpty(true);
        table.setNumberOfGuests(4);

        //then
        OrderTable actual = tableBo.create(table);
        assertThat(actual).isEqualTo(table);
    }

    @DisplayName("사용자는 테이블의 착석 여부만 변경할 수 있다")
    @Test
    void changeEmpty() {
        //given
        given(orderTableDao.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(false);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        //when
        OrderTable actual = tableBo.changeEmpty(orderTable.getId(), orderTable);

        //then
        assertThat(actual.isEmpty()).isEqualTo(orderTable.isEmpty());
    }

    @DisplayName("등록된 테이블의 착석 여부를 변경할 수 있다")
    @Test
    void changeEmpty_with_registered_table() {
        //given
        given(orderTableDao.findById(orderTable.getId()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 그룹에 속하지 않은 테이블의 착석 여부를 변경할 수 없다")
    @Test
    void changeEmpty_with_table_group() {
        //given
        orderTable.setTableGroupId(1L);

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("'식사완료' 상태가 아닌 테이블은 착석여부를 변경할 수 없다")
    @Test
    void changeEmpty_with_order_status() {
        //given
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용자는 테이블의 착석 손님 수를 변경할 수 있다")
    @Test
    void changeNumberOfGuests() {
        //given
        given(orderTableDao.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable))
                .willReturn(orderTable);

        orderTable.setNumberOfGuests(4);

        //when
        OrderTable actual = tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

        //then
        assertThat(actual.getNumberOfGuests()).isEqualTo(4);
    }

    @DisplayName("착석 손님 수는 0명 이상이어야 한다")
    @Test
    void changeNumberOfGuests_with_greater_and_equal_than_zero() {
        //given
        orderTable.setNumberOfGuests(-1);

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 테이블의 착석 손님 수를 변경할 수 없다")
    @Test
    void changeNumberOfGuests_with_registered_table() {
        //given
        given(orderTableDao.findById(orderTable.getId()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석 상태가 아닌 테이블의 손님수를 변경할 수 없다")
    @Test
    void changeNumberOfGuests_with_table_empty() {
        //given
        orderTable.setEmpty(true);

        //when
        //then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용자는 등록된 테이블의 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        given(orderTableDao.findAll()).willReturn(Arrays.asList(orderTable, orderTable2));

        //when
        List<OrderTable> actual = tableBo.list();

        //then
        assertThat(actual).containsExactlyInAnyOrder(orderTable, orderTable2);
    }
}
