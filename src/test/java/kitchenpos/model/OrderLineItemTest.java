package kitchenpos.model;

public class OrderLineItemTest {

    public static OrderLineItem of() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(MenuTest.ofHalfAndHalf().getId());
        return orderLineItem;
    }
}