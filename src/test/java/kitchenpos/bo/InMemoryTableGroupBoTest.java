package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import kitchenpos.support.OrderBuilder;
import kitchenpos.support.OrderTableBuilder;
import kitchenpos.support.TableGroupBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InMemoryTableGroupBoTest {

    private final OrderDao orderDao = new InMemoryOrderDao();
    private final OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private final TableGroupDao tableGroupDao = new InMemoryTableGroupDao();

    private TableGroupBo tableGroupBo;

    @BeforeEach
    void setup (){
        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);

        TableGroup tableGroup = new TableGroupBuilder()
            .id(1L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable1 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(null)
            .numberOfGuests(2)
            .empty(true)
            .build();

        tableGroup.addOrderTable(orderTable1);

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(2L)
            .tableGroupId(null)
            .numberOfGuests(3)
            .empty(true)
            .build();

        tableGroup.addOrderTable(orderTable2);

        TableGroup tableGroup2 = new TableGroupBuilder()
            .id(2L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable3 = new OrderTableBuilder()
            .id(3L)
            .tableGroupId(tableGroup.getId())
            .numberOfGuests(2)
            .empty(true)
            .build();

        tableGroup.addOrderTable(orderTable1);

        OrderTable orderTable4 = new OrderTableBuilder()
            .id(4L)
            .tableGroupId(tableGroup.getId())
            .numberOfGuests(3)
            .empty(true)
            .build();

        tableGroup.addOrderTable(orderTable2);

        TableGroup tableGroup3 = new TableGroupBuilder()
            .id(3L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable5 = new OrderTableBuilder()
            .id(5L)
            .tableGroupId(tableGroup3.getId())
            .numberOfGuests(4)
            .empty(false)
            .build();

        tableGroup3.addOrderTable(orderTable5);

        OrderTable orderTable6 = new OrderTableBuilder()
            .id(6L)
            .tableGroupId(tableGroup3.getId())
            .numberOfGuests(4)
            .empty(false)
            .build();

        tableGroup3.addOrderTable(orderTable6);

        Order order1 = new OrderBuilder()
            .id(1L)
            .orderTableId(5L)
            .orderStatus("COOKING")
            .orderLineItems(new ArrayList<>())
            .build();
        Order order2 = new OrderBuilder()
            .id(2L)
            .orderTableId(6L)
            .orderStatus("MEAL")
            .orderLineItems(new ArrayList<>())
            .build();

        TableGroup tableGroup4 = new TableGroupBuilder()
            .id(4L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable7 = new OrderTableBuilder()
            .id(7L)
            .tableGroupId(tableGroup4.getId())
            .numberOfGuests(4)
            .empty(false)
            .build();

        tableGroup4.addOrderTable(orderTable7);

        OrderTable orderTable8 = new OrderTableBuilder()
            .id(8L)
            .tableGroupId(tableGroup4.getId())
            .numberOfGuests(4)
            .empty(false)
            .build();

        tableGroup4.addOrderTable(orderTable8);

        Order order3 = new OrderBuilder()
            .id(3L)
            .orderTableId(7L)
            .orderStatus("COMPLETION")
            .orderLineItems(new ArrayList<>())
            .build();
        Order order4 = new OrderBuilder()
            .id(4L)
            .orderTableId(8L)
            .orderStatus("COMPLETION")
            .orderLineItems(new ArrayList<>())
            .build();

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);
        orderTableDao.save(orderTable3);
        orderTableDao.save(orderTable4);
        orderTableDao.save(orderTable5);
        orderTableDao.save(orderTable6);
        orderTableDao.save(orderTable7);
        orderTableDao.save(orderTable8);
        tableGroupDao.save(tableGroup);
        tableGroupDao.save(tableGroup2);
        tableGroupDao.save(tableGroup3);
        tableGroupDao.save(tableGroup4);
        orderDao.save(order1);
        orderDao.save(order2);
        orderDao.save(order3);
        orderDao.save(order4);

    }

    @DisplayName("TableGroup에 속한 주문테이블은 2개 이상이다. ")
    @Test
    void createOrderTableSizeUnderTwo (){
        TableGroup tableGroup = new TableGroupBuilder()
            .id(1L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(tableGroup.getId())
            .build();

        tableGroup.addOrderTable(orderTable);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("입력한 OrderTable과 DB에 저장된 OrderTable 이 동일해야 한다.")
    @Test
    void createCompareInputToDatabase (){
        TableGroup inputTableGorup1 = new TableGroupBuilder()
            .id(1L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(inputTableGorup1.getId())
            .numberOfGuests(2)
            .empty(false)
            .build();

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(inputTableGorup1.getId())
            .numberOfGuests(3)
            .empty(false)
            .build();

        inputTableGorup1.addOrderTable(orderTable);
        inputTableGorup1.addOrderTable(orderTable2);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(inputTableGorup1));
    }

    @DisplayName("자리가 비어있고, 어느 그룹에도 속해있으면 안된다.")
    @Test
    void createEmptyAndNotContainAnyGroup(){
        TableGroup inputTableGroup1 = new TableGroupBuilder()
            .id(2L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable = new OrderTableBuilder()
            .id(3L)
            .tableGroupId(inputTableGroup1.getId())
            .numberOfGuests(2)
            .empty(false)
            .build();

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(4L)
            .tableGroupId(inputTableGroup1.getId())
            .numberOfGuests(3)
            .empty(false)
            .build();

        inputTableGroup1.addOrderTable(orderTable);
        inputTableGroup1.addOrderTable(orderTable2);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.create(inputTableGroup1));

    }

    @DisplayName("테이블 그룹을 지정한다.")
    @Test
    void create(){
        TableGroup inputTableGroup1 = new TableGroupBuilder()
            .id(1L)
            .orderTables(new ArrayList<>())
            .build();

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(inputTableGroup1.getId())
            .numberOfGuests(2)
            .empty(false)
            .build();

        OrderTable orderTable2 = new OrderTableBuilder()
            .id(2L)
            .tableGroupId(inputTableGroup1.getId())
            .numberOfGuests(3)
            .empty(false)
            .build();

        inputTableGroup1.addOrderTable(orderTable);
        inputTableGroup1.addOrderTable(orderTable2);

        TableGroup savedTableGroup = tableGroupBo.create(inputTableGroup1);

        assertThat(savedTableGroup).isEqualToComparingFieldByField(inputTableGroup1);
    }

    @DisplayName("주문상태가 요리중, 식사중에는 그룹지정을 해지 할 수 없다.")
    @Test
    void deleteCannot (){
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableGroupBo.delete(3L));
    }

    @DisplayName("주문상태가 식사완료이면, 그룹지정을 해지 할 수 있다.")
    @Test
    void delete (){
        tableGroupBo.delete(4L);

        List<Long> orderTableIds = tableGroupDao.findById(4L)
            .map(TableGroup::getOrderTables)
            .orElseGet(() -> Collections.emptyList())
            .stream()
            .map(OrderTable::getId)
            .collect(Collectors.toList());

        List<Long> tableGroupIds = new ArrayList<>();

        for(Long orderTableId : orderTableIds){
            orderTableDao.findById(orderTableId)
                .ifPresent(orderTable ->{
                    if(orderTable.getTableGroupId() != null){
                        tableGroupIds.add(orderTable.getTableGroupId());
                    }
                });
        }

        assertThat(tableGroupIds.size()).isEqualTo(0);
    }

}
