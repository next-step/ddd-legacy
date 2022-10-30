package kitchenpos.common.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Quantity {

    @Column(name = "quantity", nullable = false)
    private long quantity;


    protected Quantity() {
    }

    public Quantity(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantity() {
        return this.quantity;
    }
}
