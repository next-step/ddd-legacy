package kitchenpos.product.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {
    private String name;
    private UUID id;
    private BigDecimal price;

    public ProductRequest(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

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
