package kitchenpos.order;

import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.dto.request.OrderLineItemRequest;
import kitchenpos.order.dto.request.OrderRequest;
import kitchenpos.ordertable.domain.OrderTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRequestFixture {

    public static OrderRequest 메뉴가격_메뉴항목_가격_다름(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.valueOf(11), 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, "주소", orderTable.getId());
    }

    public static OrderRequest 주문항목_비어있음(OrderTable orderTable) {
        return new OrderRequest(null, OrderType.TAKEOUT, "주소", orderTable.getId());
    }

    public static OrderRequest 메뉴수량_주문항목수량_같음(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, "주소", orderTable.getId());
    }

    public static OrderRequest 메뉴수량_주문항목수량_다름(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, "주소", orderTable.getId());
    }

    public static OrderRequest 포장주문_수량0개미만(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, -1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, "주소", orderTable.getId());
    }

    public static OrderRequest orderRequest(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, null, orderTable.getId());
    }

    public static OrderRequest 배달주문_주소없음(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.DELIVERY, null, orderTable.getId());
    }

    public static OrderRequest 매장주문(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, OrderType.EAT_IN, null, orderTable.getId());
    }

    public static OrderRequest 주문타입_NULL(Menu menu, OrderTable orderTable) {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        return new OrderRequest(orderLineItemRequests, null, null, orderTable.getId());
    }
}


