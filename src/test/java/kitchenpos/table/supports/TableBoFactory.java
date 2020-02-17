package kitchenpos.table.supports;

import java.util.Collections;
import java.util.List;

import kitchenpos.bo.TableBo;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.order.supports.OrderDaoWithCollection;

public class TableBoFactory {

    public static final long EXIST_ORDER_TABLE_ID = 1L;

    private TableBoFactory() { }

    public static TableBo withFixtures(OrderTable existTable) {
        return withFixtures(existTable, Collections.emptyList());
    }

    public static TableBo withFixtures(OrderTable existTable, List<Order> relatedOrders) {
        OrderTableDao orderTableDao = new OrderTableDaoWithCollection();
        existTable.setId(EXIST_ORDER_TABLE_ID);
        orderTableDao.save(existTable);
        relatedOrders.forEach(o -> o.setOrderTableId(existTable.getId()));
        return new TableBo(new OrderDaoWithCollection(relatedOrders),
                           orderTableDao);
    }
}
