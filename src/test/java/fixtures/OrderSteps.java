package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;


public class OrderSteps {

    private OrderTableRepository orderTableRepository;

    public OrderSteps(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }


    public Order 주문을_생성한다(Menu menu, OrderType orderType) {

        OrderLineItem orderLineItems = new OrderLineItemBuilder()
                .withMenu(menu)
                .withPrice(BigDecimal.valueOf(10_000))
                .build();

        OrderTable orderTable = orderTableRepository.save(new OrderTableBuilder()
                .anOrderTable()
                .build());

        return new OrderBuilder()
                .withOrderType(orderType)
                .withOrderLineItems(List.of(orderLineItems))
                .withOrderTable(orderTable)
                .build();
    }

}

