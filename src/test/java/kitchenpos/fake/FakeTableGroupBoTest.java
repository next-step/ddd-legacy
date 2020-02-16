package kitchenpos.fake;

import kitchenpos.bo.TableGroupBo;
import kitchenpos.builder.OrderBuilder;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.builder.TableGroupBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeTableGroupBoTest {

    private TableGroupBo tableGroupBo;

    private OrderDao orderDao = new FakeOrderDao();

    private OrderTableDao orderTableDao = new FakeOrderTableDao();

    private TableGroupDao tableGroupDao = new FakeTableGroupDao();

    @BeforeEach
    void setUp() {
        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);
    }

    @DisplayName("테이블 그룹 정상 생성")
    @Test
    void create() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build()
                ;

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        assertThat(savedTableGroup.getId()).isEqualTo(tableGroup.getId());
        assertThat(savedTableGroup.getOrderTables()).containsAll(tableGroup.getOrderTables());
    }

    @DisplayName("테이블이 하나일때 그룹 생성 에러")
    @Test
    void createFailByTableLessTwo() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        List<OrderTable> orderTables = Arrays.asList(orderTable1);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 없을때 그룹 생성 에러")
    @Test
    void createFailByTableNotExist() {
        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("등록되지 않은 테이블로 그룹 생성시 에러")
    @Test
    void createFailByTableNotSaved() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable3 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(3L)
                .setNumberOfGuests(0)
                .build()
                ;

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2, orderTable3);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);
        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("그룹화 되어 있는 테이블을 그룹화 시도시 에러")
    @Test
    void createFailByTableGroupExist() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setTableGroupId(2L)
                .setId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setNumberOfGuests(0)
                .build()
                ;

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);
        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 정상 삭제")
    @Test
    void delete() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setTableGroupId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setTableGroupId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);
        tableGroupDao.save(tableGroup);

        tableGroupBo.delete(tableGroup.getId());

        assertThat(orderTable1.getTableGroupId()).isNull();
        assertThat(orderTable2.getTableGroupId()).isNull();
    }

    @DisplayName("주문 상태가 요리 중 이거나 식사 중인 테이블은 삭제시 에러")
    @Test
    void deleteFailByStatus() {
        OrderTable orderTable1 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(1L)
                .setNumberOfGuests(0)
                .setTableGroupId(1L)
                .build()
                ;

        OrderTable orderTable2 = new OrderTableBuilder()
                .setEmpty(true)
                .setId(2L)
                .setTableGroupId(1L)
                .setNumberOfGuests(0)
                .build()
                ;

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = new TableGroupBuilder()
                .setId(1L)
                .setOrderTables(orderTables)
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;

        Order order = new OrderBuilder()
                .setId(1L)
                .setOrderedTime(LocalDateTime.now())
                .setOrderTableId(1L)
                .setOrderStatus(OrderStatus.COOKING.name())
                .build();

        orderDao.save(order);
        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);
        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(tableGroup.getId()));
    }
}
