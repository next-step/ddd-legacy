package kitchenpos.menu.menu.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuProductRequest {

    private BigDecimal quantity;
    private UUID productId;

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public UUID getProductId() {
        return this.productId;
    }
}
