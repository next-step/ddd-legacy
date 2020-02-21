package kitchenpos.fake;

import kitchenpos.TestFixture;
import kitchenpos.bo.TableGroupBo;
import kitchenpos.builder.TableGroupBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeTableGroupBoTest {
    public static final long LONG_ONE = 1L;
    public static final long LONG_TWO = 2L;

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
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        assertAll(
                () -> assertThat(savedTableGroup.getId()).isEqualTo(tableGroup.getId()),
                () -> assertThat(savedTableGroup.getOrderTables()).containsAll(tableGroup.getOrderTables())
        );
    }

    @DisplayName("테이블이 하나일때 그룹 생성 에러")
    @Test
    void createFailByTableLessTwo() {
        TableGroup tableGroup = TestFixture.generateTableGroupHasOneOrderTable();

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 없을때 그룹 생성 에러")
    @Test
    void createFailByTableNotExist() {
        TableGroup tableGroup = new TableGroupBuilder()
                .setId(LONG_ONE)
                .setCreatedDate(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("등록되지 않은 테이블로 그룹 생성시 에러")
    @Test
    void createFailByTableNotSaved() {
        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("그룹화 되어 있는 테이블을 그룹화 시도시 에러")
    @Test
    void createFailByTableGroupExist() {
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        orderTable1.setTableGroupId(2L);

        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();
        orderTable2.setTableGroupId(2L);

        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹 정상 삭제")
    @Test
    void delete() {
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        orderTable1.setTableGroupId(LONG_TWO);

        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();
        orderTable2.setTableGroupId(LONG_TWO);

        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        tableGroupDao.save(tableGroup);

        tableGroupBo.delete(tableGroup.getId());

        assertAll(
                () -> assertThat(orderTable1.getTableGroupId()).isNull(),
                () -> assertThat(orderTable2.getTableGroupId()).isNull()
        );
    }

    @DisplayName("주문 상태가 요리 중 이거나 식사 중인 테이블은 삭제시 에러")
    @Test
    void deleteFailByStatus() {
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        orderTable1.setTableGroupId(LONG_TWO);

        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();
        orderTable2.setTableGroupId(LONG_TWO);

        TableGroup tableGroup = TestFixture.generateTableGroupOne();

        Order order = TestFixture.generateOrderCooking();
        order.setOrderTableId(orderTable1.getId());

        orderDao.save(order);

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        tableGroupDao.save(tableGroup);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(tableGroup.getId()));
    }
}
