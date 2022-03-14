package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {

    public static class OrderLineItemBuilder {

        private Long seq;
        private Menu menu;
        private long quantity;
        private UUID menuId;
        private BigDecimal price;

        public OrderLineItemBuilder() {

        }

        public OrderLineItemBuilder seq(Long seq) {
            this.seq = seq;
            return this;
        }

        public OrderLineItemBuilder menu(Menu menu) {
            this.menu = menu;
            return this;
        }

        public OrderLineItemBuilder quantity(long quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderLineItemBuilder menuId(UUID menuId) {
            this.menuId = menuId;
            return this;
        }

        public OrderLineItemBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderLineItem build() {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setSeq(this.seq);
            orderLineItem.setMenu(this.menu);
            orderLineItem.setQuantity(this.quantity);
            orderLineItem.setMenuId(this.menuId);
            orderLineItem.setPrice(this.price);
            return orderLineItem;
        }
    }
}
