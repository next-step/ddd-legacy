package kitchenpos.menu.menu.dto.request;

import java.util.UUID;

public class MenuProductRequest {

    private long quantity;
    private UUID productId;

    public MenuProductRequest(UUID id, long quantity) {
        this.productId = id;
        this.quantity = quantity;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public UUID getProductId() {
        return this.productId;
    }
}
