package kitchenpos.product.dto.request;

import java.math.BigDecimal;

public class ChangePriceRequest {

    private BigDecimal price;

    public ChangePriceRequest(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
