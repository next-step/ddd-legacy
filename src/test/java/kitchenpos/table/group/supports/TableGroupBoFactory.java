package kitchenpos.table.group.supports;

import java.util.Collections;
import java.util.List;

import kitchenpos.bo.TableGroupBo;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;
import kitchenpos.order.supports.OrderDaoWithCollection;
import kitchenpos.table.supports.OrderTableDaoWithCollection;

public class TableGroupBoFactory {

    public static final long EXIST_TABLE_GROUP_ID = 1L;

    private TableGroupBoFactory() { }

    public static TableGroupBo withFixtures(List<OrderTable> relatedTables) {
        return new TableGroupBo(new OrderDaoWithCollection(),
                                new OrderTableDaoWithCollection(relatedTables),
                                new TableGroupDaoWithCollection());
    }

    public static TableGroupBo withFixtures(TableGroup tableGroup,
                                            OrderTable relatedTable,
                                            List<Order> relatedOrders) {
        TableGroupDao tableGroupDao = new TableGroupDaoWithCollection();
        tableGroup.setId(EXIST_TABLE_GROUP_ID);
        tableGroupDao.save(tableGroup);
        relatedTable.setTableGroupId(tableGroup.getId());
        tableGroup.setOrderTables(Collections.singletonList(relatedTable));

        OrderTableDao orderTableDao = new OrderTableDaoWithCollection();
        orderTableDao.save(relatedTable);

        relatedOrders.forEach(o -> o.setOrderTableId(relatedTable.getId()));
        return new TableGroupBo(new OrderDaoWithCollection(relatedOrders),
                                orderTableDao,
                                tableGroupDao);
    }
}
