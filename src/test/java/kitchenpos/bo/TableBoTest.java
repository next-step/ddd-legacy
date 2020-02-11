package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.MenuGroup;
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
import java.util.stream.LongStream;

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

    private MenuGroup mockMenuGroup;
    private OrderTable mockOrderTable;
    private List<OrderTable> mockOrderTables;

    @BeforeEach
    void beforeEach() {
        mockMenuGroup = new MenuGroup();
        mockMenuGroup.setId(1L);
        mockMenuGroup.setName("메뉴그룹1");

        mockOrderTable = new OrderTable();
        mockOrderTable.setId(1L);
//        mockOrderTable.setTableGroupId(1L);
        mockOrderTable.setNumberOfGuests(5);
        mockOrderTable.setEmpty(false);

        mockOrderTables = new ArrayList<>();

        LongStream.range(1, 100).forEach(i -> {
            OrderTable orderTable = new OrderTable();
            orderTable.setId(i);
            orderTable.setTableGroupId(i);
            orderTable.setNumberOfGuests((int) i);
            orderTable.setEmpty((int) i % 2 == 0);

            mockOrderTables.add(orderTable);
        });
    }

    @DisplayName("새로운 테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable newOrderTable = new OrderTable();
        newOrderTable.setTableGroupId(mockMenuGroup.getId());
        newOrderTable.setNumberOfGuests(5);
        newOrderTable.setEmpty(true);

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
        OrderTable updateOrderTable = new OrderTable();
        updateOrderTable.setId(1L);
        updateOrderTable.setTableGroupId(mockMenuGroup.getId());
        updateOrderTable.setNumberOfGuests(5);
        updateOrderTable.setEmpty(true);

        given(orderTableDao.save(updateOrderTable)).willReturn(updateOrderTable);

        // when
        OrderTable result = tableBo.create(updateOrderTable);

        // then
        assertThat(result.getId()).isEqualTo(updateOrderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(updateOrderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(updateOrderTable.getNumberOfGuests());
        assertThat(result.isEmpty()).isEqualTo(updateOrderTable.isEmpty());
    }

    @DisplayName("전체 테이블 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        given(orderTableDao.findAll()).willReturn(mockOrderTables);

        // when
        List<OrderTable> result = tableBo.list();

        // then
        assertThat(result.size()).isEqualTo(mockOrderTables.size());
        assertThat(result.get(0).getId()).isEqualTo(mockOrderTables.get(0).getId());
        assertThat(result.get(0).getTableGroupId()).isEqualTo(mockOrderTables.get(0).getTableGroupId());
        assertThat(result.get(0).getNumberOfGuests()).isEqualTo(mockOrderTables.get(0).getNumberOfGuests());
        assertThat(result.get(0).isEmpty()).isEqualTo(mockOrderTables.get(0).isEmpty());
    }

    @DisplayName("테이블의 공석여부를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean empty) {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setEmpty(empty);

        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(mockOrderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(mockOrderTable)).willAnswer(invocation -> {
            mockOrderTable.setEmpty(pOrderTable.isEmpty());
            return mockOrderTable;
        });

        // when
        OrderTable result = tableBo.changeEmpty(mockOrderTable.getId(), pOrderTable);

        // then
        assertThat(result.getId()).isEqualTo(mockOrderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(mockOrderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(mockOrderTable.getNumberOfGuests());
        assertThat(result.isEmpty()).isEqualTo(empty);
    }

    @DisplayName("테이블 공석여부 변경 시, 해당 테이블이 테이블그룹에 포함된 경우 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cannotChangeEmptyWhenIncludedByTableGroup(boolean empty) {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setEmpty(empty);

        mockOrderTable.setTableGroupId(1L);

        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(mockOrderTable.getId(), pOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 공석여부 변경 시, 테이블에서 발생한 주문들의 주문상태가 완료인 경우에만 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cannotChangeEmptyWhenOrdersAreNotCompleted(boolean empty) {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setEmpty(empty);

        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(mockOrderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeEmpty(mockOrderTable.getId(), pOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 게스트 수를 수정할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 200})
    void changeNumberOfGuests(int numberOfGuests) {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setNumberOfGuests(numberOfGuests);

        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));
        given(orderTableDao.save(mockOrderTable)).willAnswer(invocation -> {
            mockOrderTable.setNumberOfGuests(numberOfGuests);
            return mockOrderTable;
        });

        // when
        OrderTable result = tableBo.changeNumberOfGuests(mockOrderTable.getId(), pOrderTable);

        // then
        assertThat(result.getId()).isEqualTo(mockOrderTable.getId());
        assertThat(result.getTableGroupId()).isEqualTo(mockOrderTable.getTableGroupId());
        assertThat(result.getNumberOfGuests()).isEqualTo(numberOfGuests);
        assertThat(result.isEmpty()).isEqualTo(mockOrderTable.isEmpty());
    }

    @DisplayName("테이블의 게스트 수 수정 시, 게스트 수는 0명 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-1000, -100, -10, -1})
    void changeNumberOfGuestsOnlyWhenEqualsOrLargerThan0(int numberOfGuests) {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setNumberOfGuests(numberOfGuests);

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(mockOrderTable.getId(), pOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 게스트 수 수정 시, 테이블이 공석 상태라면 수정할 수 없다.")
    @Test
    void changeNumberOfGuestsOnlyWhenNotEmpty() {
        // given
        OrderTable pOrderTable = new OrderTable();
        pOrderTable.setNumberOfGuests(5);

        mockOrderTable.setEmpty(true);

        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            tableBo.changeNumberOfGuests(mockOrderTable.getId(), pOrderTable);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
