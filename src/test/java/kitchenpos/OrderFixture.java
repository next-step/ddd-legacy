package kitchenpos;

import kitchenpos.domain.*;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderFixture {

    public static Order 주문_Request(OrderType type) {
        Order order = new Order();
        order.setType(type);
        return order;
    }

    public static Order 주문_Request(OrderType type, List<OrderLineItem> lineItems) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(lineItems);
        return order;
    }

    public static Order 주문_Request(OrderTable table, OrderStatus status){
        Order order = new Order();
        order.setStatus(status);
        order.setOrderTable(table);
        return order;
    }

    public static Stream<Arguments> orderStatusNotCompleted() {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status != OrderStatus.COMPLETED) // 완료된 상태 제외
                .map(Arguments::of)
                .collect(Collectors.toList())
                .stream();
    }

    public static Stream<Arguments> orderTypeNotEatIn() {
        return Arrays.stream(OrderType.values())
                .filter(type -> type != OrderType.EAT_IN) // 매장 주문 제외
                .map(Arguments::of)
                .collect(Collectors.toList())
                .stream();
    }

    public static OrderLineItem 주문상품_Request(Menu menu, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

}
