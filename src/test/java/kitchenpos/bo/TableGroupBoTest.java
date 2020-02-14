package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {
    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @Mock private TableGroupDao tableGroupDao;
    @InjectMocks private TableGroupBo tableGroupBo;

    private TableGroup tableGroup;
    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private List<OrderTable> orderTableList;

    @BeforeEach
    void setup() {
        this.orderTable1 = new OrderTable();
        this.orderTable1.setId(1L);
        this.orderTable1.setTableGroupId(null);
        this.orderTable1.setEmpty(true);
        this.orderTable1.setNumberOfGuests(2);

        this.orderTable2 = new OrderTable();
        this.orderTable2.setId(2L);
        this.orderTable2.setTableGroupId(null);
        this.orderTable2.setEmpty(true);
        this.orderTable2.setNumberOfGuests(2);

        this.orderTableList = new ArrayList<>();
        this.orderTableList.add(orderTable1);
        this.orderTableList.add(orderTable2);

        this.tableGroup = new TableGroup();
        this.tableGroup.setId(1L);
        this.tableGroup.setCreatedDate(LocalDateTime.now());
        this.tableGroup.setOrderTables(orderTableList);

    }

    @DisplayName("테이블그룹을 생성할 수 있다.")
    @Test
    void create() {
        //given
        List<OrderTable> givenOrderTableList = orderTableList;
        TableGroup givenTableGroup = tableGroup;
        given(orderTableDao.findAllByIdIn(any(List.class)))
                .willReturn(givenOrderTableList);
        given(tableGroupDao.save(any(TableGroup.class)))
                .willReturn(givenTableGroup);

        //when
        TableGroup actualTableGroup = tableGroupBo.create(givenTableGroup);

        //then
        assertThat(actualTableGroup.getId())
                .isEqualTo(givenTableGroup.getId());
    }

    @DisplayName("주문이 없는 테이블은 테이블그룹으로 생성할 수 없다.")
    @ParameterizedTest
    @NullSource
    void createWithoutOrder(List<OrderTable> orderTableList) {
        //given
        TableGroup givenTableGroup = tableGroup;
        givenTableGroup.setOrderTables(orderTableList);

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(givenTableGroup); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 경우에도 두개 이상의 테이블만 테이블그룹으로 생성할 수 있다.")
    @Test
    void createOverTwoTables() {
        //given
        OrderTable givenOrderTable = orderTable1;
        List<OrderTable> givenOrderTableList = orderTableList;
        givenOrderTableList.remove(givenOrderTable);
        TableGroup givenTableGroup = tableGroup;

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(givenTableGroup); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 테이블수와 생성하려는 테이블수가 같아야 한다.")
    @Test
    void createNumberOfTable() {
        //given
        TableGroup givenTableGroup = tableGroup;

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(givenTableGroup); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹을 생성하는 대상 테이블은 비어있어야 한다.")
    @ParameterizedTest
    @ValueSource(booleans = {false})
    void createEmptyTable(boolean isEmpty) {
        //given
        OrderTable givenOrderTable = orderTable1;
        givenOrderTable.setEmpty(isEmpty);
        TableGroup givenTableGroup = tableGroup;

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.create(givenTableGroup); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹을 삭제할 수 있다.")
    @ParameterizedTest
    @MethodSource("booleanAndIntProvider")
    void delete(boolean isExists, int numberOfDeletedTables) {
        //given
        List<OrderTable> givenOrderTableList = orderTableList;
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(givenOrderTableList);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(any(List.class),any(List.class)))
                .willReturn(isExists);

        //when
        tableGroupBo.delete(this.tableGroup.getId());

        //then
        verify(orderTableDao,times(numberOfDeletedTables))
                .save(any(OrderTable.class));
    }

    static Stream<Arguments> booleanAndIntProvider() {
        return Stream.of(Arguments.arguments(false, 2));
    }

    @DisplayName("테이블상태가 조리중: COOKING, 고객이 식사중인 주문: MEAL 인 경우는 삭제할 수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true})
    void deleteByStatus(boolean isExists) {
        //given
        List<OrderTable> givenOrderTableList = orderTableList;
        TableGroup givenTableGroup = tableGroup;

        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(givenOrderTableList);
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(any(List.class),any(List.class)))
                .willReturn(isExists);

        //when
        //then
        assertThatThrownBy(() -> {
            tableGroupBo.delete(givenTableGroup.getId()); })
                .isInstanceOf(IllegalArgumentException.class);
    }
}
