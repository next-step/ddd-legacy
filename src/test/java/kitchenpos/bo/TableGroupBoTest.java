package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private static TableGroup expectedTableGroup;

    @BeforeAll
    static void setup() {

        expectedTableGroup = new TableGroup.Builder()
                .id(1L)
                .createdDate(LocalDateTime.now())
                .orderTables(new ArrayList<>())
                .build();

        OrderTable expectedOrderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();
        expectedTableGroup.addOrderTable(expectedOrderTable1);

        OrderTable expectedOrderTable2 = new OrderTable.Builder()
                .id(2L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();
        expectedTableGroup.addOrderTable(expectedOrderTable2);

    }

    @DisplayName("TableGroup 에 등록된 주문테이블이 없으면 IllegalArgumentException이 발생한다.")
    @Test
    void createWithEmptyOrderTable() {
        TableGroup tableGroup = new TableGroup.Builder()
                .id(1L)
                .orderTables(Collections.EMPTY_LIST)
                .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("입력받은 TableGroup에서 orderTable 중복이 발생하면 IllegalArgumentException이 발생한다.")
    @Test
    void createDuplicatedOrderTable() {
        TableGroup tableGroup = new TableGroup.Builder()
                .id(1L)
                .orderTables(new ArrayList<>())
                .build();

        OrderTable orderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(null)
                .numberOfGuests(4)
                .empty(true)
                .build();

        //중복입력
        tableGroup.addOrderTable(orderTable1);
        tableGroup.addOrderTable(orderTable1);

        List<OrderTable> orderTablesFromDb = new ArrayList<>();
        orderTablesFromDb.add(orderTable1);

        given(orderTableDao.findAllByIdIn(Arrays.asList(orderTable1.getId(), orderTable1.getId())))
                .willReturn(orderTablesFromDb);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("주문테이블이 비어있지 않거나, TableGroupId가 설정된 경우 IllegalArgumentException이 발생한다.")
    @Test
    void createAssignedOrderTable() {
        TableGroup tableGroup = new TableGroup.Builder()
                .id(1L)
                .orderTables(new ArrayList<>())
                .build();

        OrderTable orderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(true)
                .build();
        OrderTable orderTable2 = new OrderTable.Builder()
                .id(2L)
                .tableGroupId(null)
                .numberOfGuests(4)
                .empty(false)
                .build();

        tableGroup.addOrderTable(orderTable1);
        tableGroup.addOrderTable(orderTable2);

        List<OrderTable> orderTablesFromDb = new ArrayList<>();
        orderTablesFromDb.add(orderTable1);
        orderTablesFromDb.add(orderTable2);

        given(orderTableDao.findAllByIdIn(Arrays.asList(orderTable1.getId(), orderTable1.getId())))
                .willReturn(orderTablesFromDb);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("TableGroup 등록을 성공한다.")
    @Test
    void create() {

        TableGroup tableGroup = new TableGroup.Builder()
                .orderTables(new ArrayList<>())
                .build();

        OrderTable orderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(null)
                .numberOfGuests(4)
                .empty(true)
                .build();
        OrderTable orderTable2 = new OrderTable.Builder()
                .id(2L)
                .tableGroupId(null)
                .numberOfGuests(4)
                .empty(true)
                .build();

        tableGroup.addOrderTable(orderTable1);
        tableGroup.addOrderTable(orderTable2);

        List<OrderTable> orderTablesFromDb = new ArrayList<>();
        orderTablesFromDb.add(orderTable1);
        orderTablesFromDb.add(orderTable2);

        given(orderTableDao.findAllByIdIn(Arrays.asList(orderTable1.getId(), orderTable2.getId())))
                .willReturn(orderTablesFromDb);

        //expected
        TableGroup expectedTableGroup = new TableGroup.Builder()
                .id(1L)
                .createdDate(LocalDateTime.now())
                .orderTables(new ArrayList<>())
                .build();

        given(tableGroupDao.save(tableGroup)).willReturn(expectedTableGroup);

        OrderTable expectedOrderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();
        expectedTableGroup.addOrderTable(expectedOrderTable1);

        OrderTable expectedOrderTable2 = new OrderTable.Builder()
                .id(2L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();

        expectedTableGroup.addOrderTable(expectedOrderTable2);

        given(orderTableDao.save(expectedOrderTable1)).willReturn(expectedOrderTable1);
        given(orderTableDao.save(expectedOrderTable2)).willReturn(expectedOrderTable2);

        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        assertThat(savedTableGroup).isEqualToComparingFieldByField(expectedTableGroup);
    }


    @DisplayName("주문테이블의 상태가 COOKING 이거나 MEAL 상태면 그룹테이블 지정을 해지 할 수 없다.")
    @Test
    public void deleteWithOrderTableStatusIsOccupied(final String orderStatus) {
        TableGroup tableGroup = new TableGroup.Builder()
                .id(1L)
                .orderTables(new ArrayList<>())
                .createdDate(LocalDateTime.now())
                .build();

        OrderTable orderTable1 = new OrderTable.Builder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();
        tableGroup.addOrderTable(orderTable1);

        OrderTable orderTable2 = new OrderTable.Builder()
                .id(2L)
                .tableGroupId(1L)
                .numberOfGuests(4)
                .empty(false)
                .build();
        tableGroup.addOrderTable(orderTable2);

        given(orderTableDao.findAllByTableGroupId(expectedTableGroup.getId())).willReturn(tableGroup.getOrderTables());

        List<Long> orderTablesIds = new ArrayList<>();
        orderTablesIds.add(1L);
        orderTablesIds.add(2L);

        //상태표현을 어떻게 해야하는지 의문.
        given(orderDao.existsByOrderTableIdInAndOrderStatusIn(orderTablesIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(tableGroup.getId()));
    }

}
