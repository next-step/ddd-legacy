package kitchenpos.order.supports;

import java.util.Collections;
import java.util.List;

import kitchenpos.bo.OrderBo;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.menu.supports.MenuDaoWithCollection;
import kitchenpos.model.Menu;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import kitchenpos.table.supports.OrderTableDaoWithCollection;

public class OrderBoFactory {

    public static final long EXIST_ORDER_ID = 1L;
    public static final long EXIST_TABLE_ID = 1L;

    public static OrderBo withFixture(Order order) {
        OrderDao orderDao = new OrderDaoWithCollection();
        order.setId(EXIST_ORDER_ID);
        orderDao.save(order);
        return new OrderBo(new MenuDaoWithCollection(),
                           new OrderDaoWithCollection(Collections.singletonList(order)),
                           new OrderLineItemDaoWithCollection(),
                           new OrderTableDaoWithCollection());
    }

    public static OrderBo withFixture(OrderTable orderedTable,
                                      List<Menu> orderedMenus) {
        OrderTableDao orderTableDao = new OrderTableDaoWithCollection();
        orderedTable.setId(EXIST_TABLE_ID);
        orderTableDao.save(orderedTable);

        MenuDao menuDao = new MenuDaoWithCollection();
        orderedMenus.forEach(menuDao::save);

        return new OrderBo(menuDao,
                           new OrderDaoWithCollection(),
                           new OrderLineItemDaoWithCollection(),
                           orderTableDao);
    }
}
