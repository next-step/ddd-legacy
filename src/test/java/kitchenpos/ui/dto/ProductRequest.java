package kitchenpos.ui.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {

    private UUID id;
    private String name;
    private BigDecimal price;

    public ProductRequest() {
    }

    public ProductRequest(final String name, final long price) {
        this.name = name;
        this.price = BigDecimal.valueOf(price);
    }

    public ProductRequest(final long price) {
        this.price = BigDecimal.valueOf(price);
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long price() {
        return price.longValue();
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }
}
