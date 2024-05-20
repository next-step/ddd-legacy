package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;


public class OrderSteps {

    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;

    public OrderSteps(OrderRepository orderRepository, OrderTableRepository orderTableRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
    }

    public Order 주문한다(Menu menu, OrderType orderType) {

        OrderLineItem orderLineItems = 주문_아이템을_생성한다(menu);
        OrderTable orderTable = 주문테이블을_생성한다();

        return orderRepository.save(new OrderBuilder()
                .withOrderType(orderType)
                .withOrderLineItems(List.of(orderLineItems))
                .withOrderTable(orderTable)
                .withOrderStatus(OrderStatus.WAITING)
                .build());
    }

    public Order 주문을_생성한다(Menu menu, OrderType orderType) {
        OrderLineItem orderLineItems = 주문_아이템을_생성한다(menu);
        OrderTable orderTable = 주문테이블을_생성한다();
        return new OrderBuilder()
                .withOrderType(orderType)
                .withOrderLineItems(List.of(orderLineItems))
                .withOrderTable(orderTable)
                .withOrderStatus(OrderStatus.WAITING)
                .build();
    }


    public OrderTable 주문테이블을_생성한다() {
        OrderTable orderTable = orderTableRepository.save(new OrderTableBuilder()
                .anOrderTable()
                .build());
        return orderTableRepository.save(orderTable);
    }

    public OrderLineItem 주문_아이템을_생성한다(Menu menu) {
        return new OrderLineItemBuilder()
                .withMenu(menu)
                .withPrice(BigDecimal.valueOf(10_000))
                .build();
    }
}

