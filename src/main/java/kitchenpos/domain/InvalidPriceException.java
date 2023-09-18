package kitchenpos.domain;

import java.math.BigDecimal;

public class InvalidPriceException extends IllegalArgumentException {
    private static final String MESSAGE = "이름은 null이거나 음수일 수 없습니다. 현재 값: [%s]";

    public InvalidPriceException(BigDecimal price) {
        super(String.format(MESSAGE, price));
    }
}
