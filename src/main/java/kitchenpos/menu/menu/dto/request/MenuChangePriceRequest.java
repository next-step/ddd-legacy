package kitchenpos.menu.menu.dto.request;

import java.math.BigDecimal;

public class MenuChangePriceRequest {

    private BigDecimal price;

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
