package kitchenpos.commons;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderTableGenerator {
    @Autowired
    private OrderTableService orderTableService;

    public OrderTable generate() {
        OrderTable orderTable = this.generateRequest();
        return orderTableService.create(orderTable);
    }

    public OrderTable generateRequest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("order table 1");
        return orderTable;
    }

}
