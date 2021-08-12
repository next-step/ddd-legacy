package kitchenpos.menu.fixture;

import java.util.UUID;

public class MenuProductSaveRequest {
    private UUID productId;
    private int quantity;

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public MenuProductSaveRequest(final UUID productId, final int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
