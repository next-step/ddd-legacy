package kitchenpos.menu.menu.dto.request;

import java.util.UUID;

public class MenuProductRequest {

    private long quantity;
    private UUID productId;

    public long getQuantity() {
        return this.quantity;
    }

    public UUID getProductId() {
        return this.productId;
    }
}
