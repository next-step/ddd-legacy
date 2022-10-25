package kitchenpos.order.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemRequest {
    private long quantity;
    private UUID menuId;
    private BigDecimal price;

    protected OrderLineItemRequest() {
    }

    public OrderLineItemRequest(UUID menuId, BigDecimal price) {
        this.menuId = menuId;
        this.price = price;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public UUID getMenuId() {
        return this.menuId;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
