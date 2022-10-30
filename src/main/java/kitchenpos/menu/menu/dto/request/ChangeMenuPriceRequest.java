package kitchenpos.menu.menu.dto.request;

import java.math.BigDecimal;

public class ChangeMenuPriceRequest {

    private BigDecimal price;

    public ChangeMenuPriceRequest(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
