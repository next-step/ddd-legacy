package kitchenpos.product.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {
    private String name;
    private UUID id;
    private BigDecimal price;

    public String getName() {
        return this.name;
    }

    public UUID getId() {
        return this.id;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
