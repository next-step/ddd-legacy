package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.CHICKEN_MENU;

public class OrderLineItemFixture {

    public static OrderLineItem CHICKEN_ORDER_LINE =
        OrderLineItemFixture.builder()
                            .seq(1L)
                            .menu(CHICKEN_MENU)
                            .quantity(1L)
                            .menuId(CHICKEN_MENU.getId())
                            .price(CHICKEN_MENU.getPrice())
                            .build();

    private Long seq;
    private Menu menu;
    private long quantity;
    private UUID menuId;
    private BigDecimal price;
    
    public static OrderLineItemFixture builder() {
        return new OrderLineItemFixture();
    }

    public OrderLineItemFixture seq(Long seq) {
        this.seq = seq;
        return this;
    }

    public OrderLineItemFixture menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OrderLineItemFixture quantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineItemFixture menuId(UUID menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemFixture price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
