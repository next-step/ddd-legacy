package kitchenpos.menu.menu.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Price {

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    public Price(BigDecimal price) {
        validatePrice(price);
        this.price = price;
    }

    protected Price() {
    }

    private static void validatePrice(BigDecimal price) {
        if (Objects.isNull(price)) {
            throw new IllegalArgumentException("null 일 수 없습니다.");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0원보다 커야합니다.");
        }
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
