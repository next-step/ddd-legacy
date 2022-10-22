package kitchenpos.menu.menu.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Quantity {

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;


    protected Quantity() {}

    public Quantity(BigDecimal quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private static void validateQuantity(BigDecimal quantity) {
        if (BigDecimal.ZERO.compareTo(quantity) > 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }
}
