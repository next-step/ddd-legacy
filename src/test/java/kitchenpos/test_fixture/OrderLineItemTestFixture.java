package kitchenpos.test_fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemTestFixture {
    private OrderLineItem orderLineItem;

    private OrderLineItemTestFixture(OrderLineItem orderLineItem) {
        this.orderLineItem = orderLineItem;
    }

    public static OrderLineItemTestFixture create() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = MenuTestFixture.create().getMenu();
        BigDecimal menuPrice = menu.getMenuProducts().stream()
                .map(menuProduct -> menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menuPrice);
        return new OrderLineItemTestFixture(orderLineItem);
    }

    public OrderLineItemTestFixture changeMenu(Menu menu) {
        OrderLineItem newOrderLineItem = new OrderLineItem();
        newOrderLineItem.setSeq(orderLineItem.getSeq());
        newOrderLineItem.setMenu(menu);
        newOrderLineItem.setMenuId(menu.getId());
        newOrderLineItem.setQuantity(orderLineItem.getQuantity());
        newOrderLineItem.setPrice(newOrderLineItem.getPrice());
        this.orderLineItem = newOrderLineItem;
        return this;
    }

    public OrderLineItemTestFixture changePrice(BigDecimal price) {
        OrderLineItem newOrderLineItem = new OrderLineItem();
        newOrderLineItem.setSeq(orderLineItem.getSeq());
        newOrderLineItem.setMenu(orderLineItem.getMenu());
        newOrderLineItem.setMenuId(orderLineItem.getMenuId());
        newOrderLineItem.setQuantity(orderLineItem.getQuantity());
        newOrderLineItem.setPrice(price);
        this.orderLineItem = newOrderLineItem;
        return this;
    }

    public OrderLineItemTestFixture changeQuantity(long quantity) {
        OrderLineItem newOrderLineItem = new OrderLineItem();
        newOrderLineItem.setSeq(orderLineItem.getSeq());
        newOrderLineItem.setMenu(orderLineItem.getMenu());
        newOrderLineItem.setMenuId(orderLineItem.getMenuId());
        newOrderLineItem.setQuantity(quantity);
        newOrderLineItem.setPrice(orderLineItem.getPrice());
        this.orderLineItem = newOrderLineItem;
        return this;
    }

    public OrderLineItem getOrderLineItem() {
        return orderLineItem;
    }
}
