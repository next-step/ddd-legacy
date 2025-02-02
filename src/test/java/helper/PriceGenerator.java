package helper;

import java.math.BigDecimal;

@FunctionalInterface
public interface PriceGenerator {
    BigDecimal of(long value);
}
