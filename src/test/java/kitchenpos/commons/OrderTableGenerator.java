package kitchenpos.commons;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTableGenerator {
    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    public OrderTable generate() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("order table 1");
        return orderTableService.create(orderTable);
    }

}
